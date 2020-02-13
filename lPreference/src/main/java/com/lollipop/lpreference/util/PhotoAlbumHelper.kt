package com.lollipop.lpreference.util

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.lollipop.lpreference.value.PhotoInfo

/**
 * @author lollipop
 * @date 2020-02-12 21:28
 * 相册辅助器
 */
class PhotoAlbumHelper {

    val data = ArrayList<PhotoInfo>()

    private var onCompleteCallback: ((PhotoAlbumHelper) -> Unit)? = null
    private var onErrorCallback: ((Throwable) -> Unit)? = null

    fun onComplete(callback: (PhotoAlbumHelper) -> Unit) {
        onCompleteCallback = callback
    }

    fun onError(callback: (Throwable) -> Unit) {
        onErrorCallback = callback
    }

    fun initData(context: Context) {
        doAsync({
            onErrorCallback?.invoke(it)
        }) {
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val selection = "${MediaStore.Images.Media.MIME_TYPE} = ? or " +
                    " ${MediaStore.Images.Media.MIME_TYPE} = ?"
            val selectionArgs = arrayOf("image/jpeg", "image/png")
            val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} desc"
            val columns = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DISPLAY_NAME)
            val cursor = context.contentResolver.query(uri,
                columns, selection, selectionArgs, sortOrder)
            data.clear()
            while (cursor?.moveToNext() == true) {
                val id = cursor.getLongByColumn(MediaStore.Images.Media._ID)
                val size = cursor.getIntByColumn(MediaStore.Images.Media.SIZE)
                val name = cursor.getStringByColumn(MediaStore.Images.Media.DISPLAY_NAME)
                data.add(PhotoInfo(ContentUris.withAppendedId(uri, id), size, name))
            }
            cursor?.close()
            onUI {
                onCompleteCallback?.invoke(this@PhotoAlbumHelper)
            }
        }
    }

    private fun Cursor.getStringByColumn(col: String): String {
        return getString(getColumnIndex(col))
    }

    private fun Cursor.getIntByColumn(col: String): Int {
        return getInt(getColumnIndex(col))
    }

    private fun Cursor.getLongByColumn(col: String): Long {
        return getLong(getColumnIndex(col))
    }

}