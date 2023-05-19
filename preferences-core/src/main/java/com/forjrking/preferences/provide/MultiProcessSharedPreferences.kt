package com.forjrking.preferences.provide

import android.content.*
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.Bundle
import java.lang.ref.SoftReference
import java.lang.reflect.InvocationTargetException

/**
 * 使用ContentProvider实现多进程SharedPreferences读写;<br></br>
 * 1、ContentProvider天生支持多进程访问；<br></br>
 * 2、使用内部私有BroadcastReceiver实现多进程OnSharedPreferenceChangeListener监听；<br></br>
 *
 *
 * 使用方法：AndroidManifest.xml中添加provider申明：<br></br>
 * <pre>
 * &lt;provider android:name="com.tencent.mm.sdk.patformtools.MultiProcessSharedPreferences"
 * android:authorities="com.tencent.mm.sdk.patformtools.MultiProcessSharedPreferences"
 * android:exported="false" /&gt;
 * &lt;!-- authorities属性里面最好使用包名做前缀，apk在安装时authorities同名的provider需要校验签名，否则无法安装；--!/&gt;<br></br>
</pre> *
 *
 *
 * ContentProvider方式实现要注意：<br></br>
 * 1、当ContentProvider所在进程android.os.Process.killProcess(pid)时，会导致整个应用程序完全意外退出或者ContentProvider所在进程重启；<br></br>
 * 重启报错信息：Acquiring provider <processName> for user 0: existing object's process dead；<br></br>
 * 2、如果设备处在“安全模式”下，只有系统自带的ContentProvider才能被正常解析使用，因此put值时默认返回false，get值时默认返回null；<br></br>
</processName> *
 *
 * 其他方式实现SharedPreferences的问题：<br></br>
 * 使用FileLock和FileObserver也可以实现多进程SharedPreferences读写，但是会有兼容性问题：<br></br>
 * 1、某些设备上卸载程序时锁文件无法删除导致卸载残留，进而导致无法重新安装该程序（报INSTALL_FAILED_UID_CHANGED错误）；<br></br>
 * 2、某些设备上FileLock会导致僵尸进程出现进而导致耗电；<br></br>
 * 3、僵尸进程出现后，正常进程的FileLock会一直阻塞等待僵尸进程中的FileLock释放，导致进程一直阻塞；<br></br>
 *
 * @author seven456@gmail.com
 * @version 1.0
 * @since JDK1.6
 */
class MultiProcessSharedPreferences private constructor(
    context: Context,
    name: String,
    keyAlias: String? = null
) : ContentProvider(), SharedPreferences {
    private var mContext: Context? = context
    private var mName: String? = name
    private var mKeyAlias: String? = keyAlias
    private var mIsSafeMode = false
    private var mListeners: MutableList<SoftReference<OnSharedPreferenceChangeListener>>? = null
    private var mReceiver: BroadcastReceiver? = null
    private var mUriMatcher: UriMatcher? = null
    private var mListenersCount: MutableMap<String, Int?>? = null

    private object ReflectionUtil {
        fun contentValuesNewInstance(values: HashMap<String, Any?>?): ContentValues {
            return try {
                val c = ContentValues::class.java.getDeclaredConstructor(
                    *arrayOf<Class<*>>(
                        HashMap::class.java
                    )
                ) // hide
                c.isAccessible = true
                c.newInstance(values)
            } catch (e: IllegalArgumentException) {
                throw RuntimeException(e)
            } catch (e: IllegalAccessException) {
                throw RuntimeException(e)
            } catch (e: InvocationTargetException) {
                throw RuntimeException(e)
            } catch (e: NoSuchMethodException) {
                throw RuntimeException(e)
            } catch (e: InstantiationException) {
                throw RuntimeException(e)
            }
        }

        fun editorPutStringSet(
            editor: SharedPreferences.Editor,
            key: String?,
            values: Set<String?>?
        ): SharedPreferences.Editor {
            return try {
                val method = editor.javaClass.getDeclaredMethod(
                    "putStringSet", *arrayOf(String::class.java, MutableSet::class.java)
                ) // Android 3.0
                method.invoke(editor, key, values) as SharedPreferences.Editor
            } catch (e: IllegalArgumentException) {
                throw RuntimeException(e)
            } catch (e: IllegalAccessException) {
                throw RuntimeException(e)
            } catch (e: InvocationTargetException) {
                throw RuntimeException(e)
            } catch (e: NoSuchMethodException) {
                throw RuntimeException(e)
            }
        }

        fun editorApply(editor: SharedPreferences.Editor) {
            try {
                val method = editor.javaClass.getDeclaredMethod("apply") // Android 2.3
                method.invoke(editor)
            } catch (e: IllegalArgumentException) {
                throw RuntimeException(e)
            } catch (e: IllegalAccessException) {
                throw RuntimeException(e)
            } catch (e: InvocationTargetException) {
                throw RuntimeException(e)
            } catch (e: NoSuchMethodException) {
                throw RuntimeException(e)
            }
        }
    }

    private fun checkInitAuthority(context: Context?) {
        if (AUTHORITY_URI == null) {
            var authority: String? = null
            var authorityUri = AUTHORITY_URI
            synchronized(this@MultiProcessSharedPreferences) {
                if (authorityUri == null) {
                    authority = queryAuthority(context)
                    authorityUri = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + authority)
                }
                requireNotNull(authority) { "'AUTHORITY' initialize failed." }
            }
            AUTHORITY = authority
            AUTHORITY_URI = authorityUri
        }
    }

    override fun getAll(): Map<String, *>? {
        return getValue(PATH_GET_ALL, null, null) as Map<String, *>?
    }

    override fun getString(key: String, defValue: String?): String? {
        val v = getValue(PATH_GET_STRING, key, defValue) as String?
        return v ?: defValue
    }

    // @Override // Android 3.0
    override fun getStringSet(key: String, defValues: Set<String>?): Set<String>? {
        synchronized(this) {
            val v = getValue(PATH_GET_STRING, key, defValues) as Set<String>?
            return v ?: defValues
        }
    }

    override fun getInt(key: String, defValue: Int): Int {
        val v = getValue(PATH_GET_INT, key, defValue) as Int?
        return v ?: defValue
    }

    override fun getLong(key: String, defValue: Long): Long {
        val v = getValue(PATH_GET_LONG, key, defValue) as Long?
        return v ?: defValue
    }

    override fun getFloat(key: String, defValue: Float): Float {
        val v = getValue(PATH_GET_FLOAT, key, defValue) as Float?
        return v ?: defValue
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        val v = getValue(PATH_GET_BOOLEAN, key, defValue) as Boolean?
        return v ?: defValue
    }

    override fun contains(key: String): Boolean {
        val v = getValue(PATH_CONTAINS, key, null) as Boolean?
        return v ?: false
    }

    override fun edit(): SharedPreferences.Editor {
        return EditorImpl()
    }

    override fun registerOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener) {
        synchronized(this) {
            if (mListeners == null) {
                mListeners = ArrayList()
            }
            val result = getValue(
                PATH_REGISTER_ON_SHARED_PREFERENCE_CHANGE_LISTENER,
                null,
                false
            ) as Boolean?
            if (result != null && result) {
                mListeners!!.add(SoftReference(listener))
                if (mReceiver == null) {
                    mReceiver = object : BroadcastReceiver() {
                        override fun onReceive(context: Context, intent: Intent) {
                            val name = intent.getStringExtra(KEY_NAME)
                            val keysModified = intent.getSerializableExtra(KEY) as List<String>?
                            if (mName == name && keysModified != null) {
                                val listeners =
                                    mutableListOf<SoftReference<OnSharedPreferenceChangeListener>>()
                                synchronized(this@MultiProcessSharedPreferences) {
                                    listeners.addAll(mListeners!!)
                                }
                                for (i in keysModified.indices.reversed()) {
                                    val key = keysModified[i]
                                    for (srlistener in listeners) {
                                        srlistener.get()?.onSharedPreferenceChanged(
                                            this@MultiProcessSharedPreferences,
                                            key
                                        )
                                    }
                                }
                            }
                        }
                    }
                    mContext!!.registerReceiver(mReceiver, IntentFilter(makeAction(mName)))
                }
            }
        }
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: OnSharedPreferenceChangeListener) {
        synchronized(this) {
            getValue(PATH_UNREGISTER_ON_SHARED_PREFERENCE_CHANGE_LISTENER, null, false)
            if (mListeners != null) {
                val removing: MutableList<SoftReference<OnSharedPreferenceChangeListener>> =
                    ArrayList()
                for (srlistener in mListeners!!) {
                    val listenerFromSR = srlistener.get()
                    if (listenerFromSR != null && listenerFromSR == listener) {
                        removing.add(srlistener)
                    }
                }
                for (srlistener in removing) {
                    mListeners!!.remove(srlistener)
                }
                if (mListeners!!.isEmpty() && mReceiver != null) {
                    mContext!!.unregisterReceiver(mReceiver)
                    mReceiver = null
                    mListeners = null
                }
            }
        }
    }

    inner class EditorImpl : SharedPreferences.Editor {
        private val mModified: MutableMap<String, Any?> = HashMap()
        private var mClear = false
        override fun putString(key: String, value: String?): SharedPreferences.Editor {
            synchronized(this) {
                mModified[key] = value
                return this
            }
        }

        // @Override // Android 3.0
        override fun putStringSet(key: String, values: Set<String>?): SharedPreferences.Editor {
            synchronized(this) {
                mModified[key] = if (values == null) null else HashSet(values)
                return this
            }
        }

        override fun putInt(key: String, value: Int): SharedPreferences.Editor {
            synchronized(this) {
                mModified[key] = value
                return this
            }
        }

        override fun putLong(key: String, value: Long): SharedPreferences.Editor {
            synchronized(this) {
                mModified[key] = value
                return this
            }
        }

        override fun putFloat(key: String, value: Float): SharedPreferences.Editor {
            synchronized(this) {
                mModified[key] = value
                return this
            }
        }

        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor {
            synchronized(this) {
                mModified[key] = value
                return this
            }
        }

        override fun remove(key: String): SharedPreferences.Editor {
            synchronized(this) {
                mModified[key] = null
                return this
            }
        }

        override fun clear(): SharedPreferences.Editor {
            synchronized(this) {
                mClear = true
                return this
            }
        }

        override fun apply() {
            setValue(PATH_APPLY)
        }

        override fun commit(): Boolean {
            return setValue(PATH_COMMIT)
        }

        private fun setValue(pathSegment: String): Boolean {
            return if (mIsSafeMode) { // 如果设备处在“安全模式”，返回false；
                false
            } else {
                synchronized(this@MultiProcessSharedPreferences) {
                    checkInitAuthority(mContext)
                    val selectionArgs = arrayOf(mKeyAlias, mClear.toString())
                    synchronized(this) {
                        val uri = Uri.withAppendedPath(
                            Uri.withAppendedPath(
                                AUTHORITY_URI,
                                mName
                            ), pathSegment
                        )
                        val values = ReflectionUtil.contentValuesNewInstance(
                            mModified as HashMap<String, Any?>
                        )
                        return mContext!!.contentResolver.update(
                            uri,
                            values,
                            null,
                            selectionArgs
                        ) > 0
                    }
                }
            }
        }
    }

    private fun getValue(pathSegment: String, key: String?, defValue: Any?): Any? {
        return if (mIsSafeMode) { // 如果设备处在“安全模式”，返回null；
            null
        } else {
            checkInitAuthority(mContext)
            var v: Any? = null
            val uri = Uri.withAppendedPath(Uri.withAppendedPath(AUTHORITY_URI, mName), pathSegment)
            val selectionArgs = arrayOf(mKeyAlias, key, defValue?.toString())
            val cursor = mContext!!.contentResolver.query(uri, null, null, selectionArgs, null)
            cursor?.use {
                try {
                    val bundle = it.extras
                    if (bundle != null) {
                        v = bundle[KEY]
                        bundle.clear()
                    }
                } catch (e: Exception) {
                    //not required
                }
            }
            v ?: defValue
        }
    }

    private fun makeAction(name: String?): String {
        return String.format("%1\$s_%2\$s", MultiProcessSharedPreferences::class.java.name, name)
    }

    override fun onCreate(): Boolean {
        mIsSafeMode = context?.packageManager?.isSafeMode == true
        checkInitAuthority(context)
        mUriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, PATH_WILDCARD + PATH_GET_ALL, GET_ALL)
            addURI(AUTHORITY, PATH_WILDCARD + PATH_GET_STRING, GET_STRING)
            addURI(AUTHORITY, PATH_WILDCARD + PATH_GET_INT, GET_INT)
            addURI(AUTHORITY, PATH_WILDCARD + PATH_GET_LONG, GET_LONG)
            addURI(AUTHORITY, PATH_WILDCARD + PATH_GET_FLOAT, GET_FLOAT)
            addURI(AUTHORITY, PATH_WILDCARD + PATH_GET_BOOLEAN, GET_BOOLEAN)
            addURI(AUTHORITY, PATH_WILDCARD + PATH_CONTAINS, CONTAINS)
            addURI(AUTHORITY, PATH_WILDCARD + PATH_APPLY, APPLY)
            addURI(AUTHORITY, PATH_WILDCARD + PATH_COMMIT, COMMIT)
            addURI(
                AUTHORITY, PATH_WILDCARD + PATH_REGISTER_ON_SHARED_PREFERENCE_CHANGE_LISTENER,
                REGISTER_ON_SHARED_PREFERENCE_CHANGE_LISTENER
            )
            addURI(
                AUTHORITY, PATH_WILDCARD + PATH_UNREGISTER_ON_SHARED_PREFERENCE_CHANGE_LISTENER,
                UNREGISTER_ON_SHARED_PREFERENCE_CHANGE_LISTENER
            )
        }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val name = uri.pathSegments[0]
        val keyAlias = selectionArgs!![0]
        val key = selectionArgs[1]
        val defValue = selectionArgs[2]
        val bundle = Bundle()
        when (mUriMatcher!!.match(uri)) {
            GET_ALL -> bundle.putSerializable(
                KEY,
                compatSharedPreferences(context!!, name, keyAlias).all as HashMap<String?, *>
            )

            GET_STRING -> bundle.putString(
                KEY,
                compatSharedPreferences(context!!, name, keyAlias).getString(key, defValue)
            )

            GET_INT -> bundle.putInt(
                KEY,
                compatSharedPreferences(context!!, name, keyAlias).getInt(key, defValue.toInt())
            )

            GET_LONG -> bundle.putLong(
                KEY,
                compatSharedPreferences(context!!, name, keyAlias).getLong(key, defValue.toLong())
            )

            GET_FLOAT -> bundle.putFloat(
                KEY,
                compatSharedPreferences(context!!, name, keyAlias).getFloat(key, defValue.toFloat())
            )

            GET_BOOLEAN -> bundle.putBoolean(
                KEY,
                compatSharedPreferences(context!!, name, keyAlias)
                    .getBoolean(key, java.lang.Boolean.parseBoolean(defValue))
            )

            CONTAINS -> bundle.putBoolean(
                KEY,
                compatSharedPreferences(context!!, name, keyAlias).contains(key)
            )

            REGISTER_ON_SHARED_PREFERENCE_CHANGE_LISTENER -> {
                checkInitListenersCount()
                var countInteger = mListenersCount!![name]
                val count = (countInteger ?: 0) + 1
                mListenersCount!![name] = count
                countInteger = mListenersCount!![name]
                bundle.putBoolean(KEY, count == (countInteger ?: 0))
            }

            UNREGISTER_ON_SHARED_PREFERENCE_CHANGE_LISTENER -> {
                checkInitListenersCount()
                var countInteger = mListenersCount!![name]
                val count = (countInteger ?: 0) - 1
                if (count <= 0) {
                    mListenersCount!!.remove(name)
                    bundle.putBoolean(KEY, !mListenersCount!!.containsKey(name))
                } else {
                    mListenersCount!![name] = count
                    countInteger = mListenersCount!![name]
                    bundle.putBoolean(KEY, count == (countInteger ?: 0))
                }
            }

            else -> throw IllegalArgumentException("This is Unknown Uri：$uri")
        }
        return BundleCursor(bundle)
    }

    override fun getType(uri: Uri): String? {
        throw UnsupportedOperationException("No external call")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw UnsupportedOperationException("No external insert")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        throw UnsupportedOperationException("No external delete")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        var result = 0
        val name = uri.pathSegments[0]
        val keyAlias = selectionArgs!![0]
        val preferences = compatSharedPreferences(context!!, name, keyAlias)
        when (val match = mUriMatcher!!.match(uri)) {
            APPLY, COMMIT -> {
                val hasListeners =
                    mListenersCount != null && mListenersCount!![name] != null && mListenersCount!![name]!! > 0
                var keysModified: ArrayList<String?>? = null
                var map: Map<String?, Any?> = HashMap()
                if (hasListeners) {
                    keysModified = ArrayList()
                    map = preferences.all as Map<String?, Any?>
                }
                val editor = preferences.edit()
                val clear = java.lang.Boolean.parseBoolean(selectionArgs[1])
                if (clear) {
                    if (hasListeners && map.isNotEmpty()) {
                        for ((key) in map) {
                            keysModified!!.add(key)
                        }
                    }
                    editor.clear()
                }
                for ((k, v) in values!!.valueSet()) {
                    // Android 5.L_preview : "this" is the magic value for a removal mutation. In addition,
                    // setting a value to "null" for a given key is specified to be
                    // equivalent to calling remove on that key.
                    if (v is EditorImpl || v == null) {
                        editor.remove(k)
                        if (hasListeners && map.containsKey(k)) {
                            keysModified!!.add(k)
                        }
                    } else {
                        if (hasListeners && (!map.containsKey(k) || (map.containsKey(k) && v != map[k]))) {
                            keysModified!!.add(k)
                        }
                    }
                    when (v) {
                        is String -> {
                            editor.putString(k, v)
                        }

                        is Set<*> -> {
                            ReflectionUtil.editorPutStringSet(
                                editor, k,
                                v as Set<String?>
                            ) // Android 3.0
                        }

                        is Int -> {
                            editor.putInt(k, v)
                        }

                        is Long -> {
                            editor.putLong(k, v)
                        }

                        is Float -> {
                            editor.putFloat(k, v)
                        }

                        is Boolean -> {
                            editor.putBoolean(k, v)
                        }
                    }
                }
                if (hasListeners && keysModified!!.isEmpty()) {
                    result = 1
                } else {
                    when (match) {
                        APPLY -> {
                            ReflectionUtil.editorApply(editor) // Android 2.3
                            result = 1
                            // Okay to notify the listeners before it's hit disk
                            // because the listeners should always get the same
                            // SharedPreferences instance back, which has the
                            // changes reflected in memory.
                            notifyListeners(name, keysModified)
                        }

                        COMMIT -> if (editor.commit()) {
                            result = 1
                            notifyListeners(name, keysModified)
                        }
                    }
                }
                values.clear()
            }

            else -> throw IllegalArgumentException("This is Unknown Uri：$uri")
        }
        return result
    }

    override fun onLowMemory() {
        if (mListenersCount != null) {
            mListenersCount!!.clear()
        }
        super.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        if (mListenersCount != null) {
            mListenersCount!!.clear()
        }
        super.onTrimMemory(level)
    }

    private fun checkInitListenersCount() {
        if (mListenersCount == null) {
            mListenersCount = HashMap()
        }
    }

    private fun notifyListeners(name: String, keysModified: ArrayList<String?>?) {
        if (!keysModified.isNullOrEmpty()) {
            val intent = Intent()
            intent.action = makeAction(name)
            intent.setPackage(context!!.packageName)
            intent.putExtra(KEY_NAME, name)
            intent.putExtra(KEY, keysModified)
            context!!.sendBroadcast(intent)
        }
    }

    private class BundleCursor(private var mBundle: Bundle) : MatrixCursor(arrayOf(), 0) {
        override fun getExtras(): Bundle {
            return mBundle
        }

        override fun respond(extras: Bundle): Bundle {
            mBundle = extras
            return mBundle
        }
    }

    companion object {
        private const val TAG = "MicroMsg.MultiProcessSharedPreferences"
        const val DEBUG = false
        private var AUTHORITY: String? = null

        @Volatile
        private var AUTHORITY_URI: Uri? = null
        private const val KEY = "value"
        private const val KEY_NAME = "name"
        private const val PATH_WILDCARD = "*/"
        private const val PATH_GET_ALL = "getAll"
        private const val PATH_GET_STRING = "getString"
        private const val PATH_GET_INT = "getInt"
        private const val PATH_GET_LONG = "getLong"
        private const val PATH_GET_FLOAT = "getFloat"
        private const val PATH_GET_BOOLEAN = "getBoolean"
        private const val PATH_CONTAINS = "contains"
        private const val PATH_APPLY = "apply"
        private const val PATH_COMMIT = "commit"
        private const val PATH_REGISTER_ON_SHARED_PREFERENCE_CHANGE_LISTENER =
            "registerOnSharedPreferenceChangeListener"
        private const val PATH_UNREGISTER_ON_SHARED_PREFERENCE_CHANGE_LISTENER =
            "unregisterOnSharedPreferenceChangeListener"
        private const val GET_ALL = 1
        private const val GET_STRING = 2
        private const val GET_INT = 3
        private const val GET_LONG = 4
        private const val GET_FLOAT = 5
        private const val GET_BOOLEAN = 6
        private const val CONTAINS = 7
        private const val APPLY = 8
        private const val COMMIT = 9
        private const val REGISTER_ON_SHARED_PREFERENCE_CHANGE_LISTENER = 10
        private const val UNREGISTER_ON_SHARED_PREFERENCE_CHANGE_LISTENER = 11
        private fun queryAuthority(context: Context?): String? {
            val packageInfo = try {
                context?.packageManager?.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_PROVIDERS
                )
            } catch (e: PackageManager.NameNotFoundException) {
                // not required
                null
            }
            if (packageInfo?.providers != null) {
                for (providerInfo in packageInfo.providers) {
                    if (providerInfo.name == MultiProcessSharedPreferences::class.java.name) {
                        return providerInfo.authority
                    }
                }
            }
            return null
        }

        fun getSharedPreferences(
            context: Context,
            name: String,
            keyAlias: String?
        ): SharedPreferences {
            return MultiProcessSharedPreferences(context, name, keyAlias)
        }
    }
}