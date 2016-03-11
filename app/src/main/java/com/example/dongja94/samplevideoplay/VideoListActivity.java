package com.example.dongja94.samplevideoplay;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

public class VideoListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    ListView listView;
    SimpleCursorAdapter mAdapter;

    int columnIdIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        listView = (ListView)findViewById(R.id.listView);

        String[] from = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME};
        int[] to = {R.id.image_thumbnail, R.id.text_display_name};
        mAdapter = new SimpleCursorAdapter(this, R.layout.view_list_item, null, from, to, 0);
        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (columnIndex == columnIdIndex) {
                    long id = cursor.getLong(columnIndex);
                    Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(getContentResolver(), id, MediaStore.Video.Thumbnails.MICRO_KIND, null);
                    ImageView thumbnailView = (ImageView)view;
                    thumbnailView.setImageBitmap(bitmap);
                    return true;
                }
                return false;
            }
        });
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
                Intent result = new Intent();
                result.setData(uri);
                Cursor c = (Cursor)listView.getItemAtPosition(position);
                String displayName = c.getString(c.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                String file = c.getString(c.getColumnIndex(MediaStore.Video.Media.DATA));
                result.putExtra("displayName",displayName);
                result.putExtra("file", file);
                setResult(RESULT_OK, result);
                finish();
            }
        });
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DATA},
                null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        columnIdIndex = data.getColumnIndex(MediaStore.Video.Media._ID);
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
