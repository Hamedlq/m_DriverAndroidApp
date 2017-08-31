package com.mibarim.driver.ui.activities;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.AppCompatButton;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.mibarim.driver.BootstrapApplication;
import com.mibarim.driver.BootstrapServiceProvider;
import com.mibarim.driver.R;
import com.mibarim.driver.core.Constants;
import com.mibarim.driver.core.ImageUtils;
import com.mibarim.driver.data.UserData;
import com.mibarim.driver.models.ApiResponse;
import com.mibarim.driver.models.ImageResponse;
import com.mibarim.driver.models.Plus.PassRouteModel;
import com.mibarim.driver.models.UserInfoModel;
import com.mibarim.driver.models.enums.ImageTypes;
import com.mibarim.driver.services.UserImageService;
import com.mibarim.driver.services.UserInfoService;
import com.mibarim.driver.ui.BootstrapActivity;
import com.mibarim.driver.ui.HandleApiMessages;
import com.mibarim.driver.ui.HandleApiMessagesBySnackbar;
import com.mibarim.driver.util.SafeAsyncTask;

import java.io.ByteArrayOutputStream;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * Created by Alireza on 8/24/2017.
 */

public class UserImageUploadActivity extends BootstrapActivity implements View.OnClickListener {

    private static final int RESULT_LOAD_IMAGE = 1;
    Button cameraButton;
    Button galleryButton;

    ImageView imageToUpload;
    ImageView imageTemp;
    private int USER_REQUEST_CAMERA = 2;
    private int USER_SELECT_FILE = 3;
    private boolean refreshingToken = false;
    private String authToken;
    private UserInfoModel newUserInfoModel;
    private Bitmap image;
//    @Bind(R.id.progressBar)
//    protected ProgressBar progressBar;

    ProgressDialog progressDialog;

    @Bind(R.id.continue_btn)
    protected AppCompatButton continueButton;


    private ApiResponse response;

    private String encodedImage;

    private PassRouteModel imageResponse;

    private View parentLayout;
    private String id;


    @Inject
    BootstrapServiceProvider serviceProvider;

    @Inject
    UserImageService userImageService;

    @Inject
    UserInfoService userInfoService;

    @Inject
    UserData userData;
    private int CROP_PIC_REQUEST_CODE = 2;
    private int REQUEST_TAKE_PICTURE = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_image_upload_activity);

        BootstrapApplication.component().inject(this);
//        progressBar.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);


        imageToUpload = (ImageView) findViewById(R.id.image_to_upload);
        cameraButton = (Button) findViewById(R.id.camera_button);
        galleryButton = (Button) findViewById(R.id.gallery_button);


        imageToUpload.setOnClickListener(this);

        cameraButton.setOnClickListener(this);
        galleryButton.setOnClickListener(this);
        continueButton.setOnClickListener(this);


        if (getIntent() != null && getIntent().getExtras() != null) {
            authToken = getIntent().getExtras().getString(Constants.Auth.AUTH_TOKEN);
        }

        SharedPreferences prefs = getApplicationContext().getSharedPreferences(
                "com.mibarim.main", Context.MODE_PRIVATE);
        prefs.edit().putInt("UserPhotoUploadedFirstTry", 1).apply();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gallery_button:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);

                break;

            case R.id.camera_button:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PICTURE);



/*

                UploadImage uploadImage = new UploadImage(image, "name");
                uploadImage.execute();
*/


//                getImageById(imageResponse.UserImageId);


//                selectImage();

                break;

            case R.id.continue_btn:
                finish();


        }


    }

    private void doCrop(Uri picUri) {
        try {

            Intent cropIntent = new Intent("com.android.camera.action.CROP");

            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
//            cropIntent.putExtra("spotlightX", 200); // int
//            cropIntent.putExtra("spotlightY", 200); // int

//            cropIntent.putExtra("outputX", 128);
//            cropIntent.putExtra("outputY", 128);
            cropIntent.putExtra("return-data", true);
            cropIntent.putExtra("scale", true);
            startActivityForResult(cropIntent, CROP_PIC_REQUEST_CODE);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {

            final Uri imageUri = data.getData();
            doCrop(imageUri);

/*
            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            final Bitmap selectedImage1 = BitmapFactory.decodeStream(imageStream);

            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setClassName("com.google.android.gallery3d", "com.android.gallery3d.app.CropImage");
//            File file = new File(filePath);
//            Uri uri = Uri.fromFile(file);
            intent.setData(imageUri);
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 96);
            intent.putExtra("outputY", 96);
            intent.putExtra("noFaceDetection", true);
            intent.putExtra("return-data", true);
//            startActivityForResult(intent, CROP_PIC_REQUEST_CODE);

            CropImage.activity(imageUri).setCropShape(CropImageView.CropShape.OVAL).setFixAspectRatio(true)
                    .start(this);
*/

//            String encodedImage = encodeImage(selectedImage);

/*

            imageToUpload.setImageURI(selectedImage);
            image = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();
*/
//            image = selectedImage1;
//            saveProfileImage();
        }

        if (requestCode == CROP_PIC_REQUEST_CODE) {
            if (data != null) {
                Bundle extras = data.getExtras();
                Bitmap bitmap = extras.getParcelable("data");
                image = bitmap;
//                progressBar.setVisibility(View.VISIBLE);
                progressDialog.show();
                saveProfileImage();
            }
        }

/*

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                InputStream imageStream = null;
                try {
                    imageStream = getContentResolver().openInputStream(resultUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                final Bitmap selectedImage1 = BitmapFactory.decodeStream(imageStream);
                image = selectedImage1;
                progressBar.setVisibility(View.VISIBLE);
                saveProfileImage();


            }
            if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
*/


        if (requestCode == REQUEST_TAKE_PICTURE) {
            if (data != null) {
//                Uri photo = (Uri) data.getExtras().get("data");
//                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Uri uri = data.getData();
                doCrop(uri);

/*
                CropImage.activity(uri).setCropShape(CropImageView.CropShape.OVAL).setFixAspectRatio(true)
                        .start(this);
*/


//                image = photo;
//                saveProfileImage();

            }


        }


    }


    private void saveProfileImage() {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (authToken == null) {
                    authToken = serviceProvider.getAuthToken(UserImageUploadActivity.this);
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                response = userImageService.SaveImage(authToken, encodedImage, ImageTypes.UserPic.ordinal() );
                if ((response.Errors == null || response.Errors.size() == 0) && response.Status.equals("OK")) {

                    if (response != null) {
                        for (String tripJson : response.Messages) {
                            id = tripJson.replace("\"", "");
                        }
                    }

//                    id = response.Messages.get(0);

                    return true;
                }
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
            }

            @Override
            protected void onSuccess(final Boolean uploadSuccess) throws Exception {
                super.onSuccess(uploadSuccess);
                if (!uploadSuccess) {
                    new HandleApiMessages(UserImageUploadActivity.this, response).showMessages();
                }
//                getImageById(imageResponse.UserImageId);
                getImageById(id);
                reloadUserInfo();
            }
        }.execute();
    }

    private void reloadUserInfo() {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (authToken == null) {
//                    serviceProvider.invalidateAuthToken();
//                    authToken = serviceProvider.getAuthToken(UserInfoDetailActivity.this);
                }
                newUserInfoModel = userInfoService.getUserInfo(authToken);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
            }

            @Override
            protected void onSuccess(final Boolean state) throws Exception {
                super.onSuccess(state);
                if (state) {
                    userData.insertUserInfo(newUserInfoModel);
//                    getImages(newUserInfoModel);
                }
            }
        }.execute();

    }


    public Bitmap getImageById(String imageId) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_camera);
        if (imageId == null || imageId.equals("") || imageId.equals("00000000-0000-0000-0000-000000000000")) {
            return icon;
        }
        ImageResponse imageResponse = userData.imageQuery(imageId);
        if (imageResponse != null) {
            Bitmap b = ImageUtils.loadImageFromStorage(imageResponse.ImageFilePath, imageResponse.ImageId);
            if (b != null) {
                return b;
            } else {
                getImageFromServer(imageId);
            }
        } else {
            getImageFromServer(imageId);
        }
        return icon;
    }


    private void getImageFromServer(final String imageId) {
        new SafeAsyncTask<Boolean>() {
            ImageResponse imageResponse = new ImageResponse();

            @Override
            public Boolean call() throws Exception {
                String token = serviceProvider.getAuthToken(UserImageUploadActivity.this);
                imageResponse = userInfoService.GetImageById(token, imageId);
                if (imageResponse != null && imageResponse.Base64ImageFile != null) {
                    return true;
                }

                return false;

            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                if (e instanceof android.os.OperationCanceledException) {
                    // User cancelled the authentication process (back button, etc).
                    // Since auth could not take place, lets finish this activity.
//                    finish();
                }
            }

            @Override
            protected void onSuccess(final Boolean imageLoaded) throws Exception {
                if (imageLoaded) {
                    byte[] decodedString = Base64.decode(imageResponse.Base64ImageFile, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    String path = ImageUtils.saveImageToInternalStorage(getApplicationContext(), decodedByte, imageResponse.ImageId);
                    userData.insertImage(imageResponse, path);
//                    imageToUpload.setImageURI();
                    imageToUpload.setImageBitmap(decodedByte);
//                    progressBar.setVisibility(View.GONE);
                    progressDialog.hide();

                    Toast.makeText(getBaseContext(),"عکس بارگذاری شد.",Toast.LENGTH_LONG).show();

/*

                    hideProgress();
                    if (imageLoaded) {
                        gotoBankPayPage(paymentDetailModel);
                    }
*/

                    new HandleApiMessagesBySnackbar(parentLayout, response).showMessages();
//                    setImage(imageResponse);
                }
            }
        }.execute();
    }


}
