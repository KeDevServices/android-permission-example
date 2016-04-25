package eu.kedev.fschneider.androidpermissionexample;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_INTENT = 1;
    private static final int REQUEST_READ_CONTACTS = 2;

    private ImageView mCameraImageView;
    private TextView mContactTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCameraImageView = (ImageView) findViewById(R.id.CameraExampleImageView);
        mContactTextView = (TextView) findViewById(R.id.ContactTextView);

        Button tCameraButton = (Button) findViewById(R.id.CameraButton);
        Button tSomePermissionButton = (Button) findViewById(R.id.SomePermissionButton);

        tCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tCameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(tCameraIntent, REQUEST_CAMERA_INTENT);
            }
        });

        tSomePermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionHandling();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA_INTENT && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            mCameraImageView.setImageBitmap(photo);
        }
    }

    private void permissionHandling() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Is the explanation necessary?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

                final Activity tActivty = this;

                Snackbar tSnackbar = Snackbar
                        .make(mContactTextView, "This app wants to show you your first contact.", Snackbar.LENGTH_LONG)
                        .setAction("Give permission", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(tActivty,
                                        new String[]{Manifest.permission.READ_CONTACTS},
                                        REQUEST_READ_CONTACTS);
                            }
                        });
                tSnackbar.show();
            } else {
                // For example the camera permission in a camera app needs no explanation
                // and can be directly requested.
            }

        } else {
            // If we have this permission show the information.
            getContactInfo();
        }
    }

    private void getContactInfo(){
        ContentResolver tContentResolver = getContentResolver();
        Cursor tCursor = tContentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (tCursor.getCount() > 0) {
            tCursor.moveToNext();
            String tContactID = tCursor.getString(
                    tCursor.getColumnIndex(ContactsContract.Contacts._ID));
            String tContactName = tCursor.getString(
                    tCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            mContactTextView.setText("ID: " + tContactID + "\nName: " + tContactName);
        }
    }
}