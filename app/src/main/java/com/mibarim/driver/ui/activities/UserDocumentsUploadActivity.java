package com.mibarim.driver.ui.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mibarim.driver.BootstrapApplication;
import com.mibarim.driver.BootstrapServiceProvider;
import com.mibarim.driver.R;
import com.mibarim.driver.core.Constants;
import com.mibarim.driver.core.ImageUtils;
import com.mibarim.driver.data.UserData;
import com.mibarim.driver.models.ApiResponse;
import com.mibarim.driver.models.ImageResponse;
import com.mibarim.driver.models.UserInfoModel;
import com.mibarim.driver.models.enums.ImageTypes;
import com.mibarim.driver.services.UserImageService;
import com.mibarim.driver.services.UserInfoService;
import com.mibarim.driver.ui.BootstrapActivity;
import com.mibarim.driver.ui.HandleApiMessages;
import com.mibarim.driver.ui.HandleApiMessagesBySnackbar;
import com.mibarim.driver.util.SafeAsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * Created by Alireza on 8/26/2017.
 */

public class UserDocumentsUploadActivity extends BootstrapActivity implements View.OnClickListener {

    private static final String IMAGE_TYPE_INT = "imageTypeInt";
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_OPEN_GALLERY = 2;
    private int CROP_PIC_REQUEST_CODE = 3;
    private String authToken;
    private UserInfoModel userInfoModel;
    private ApiResponse response;
    private String id;
    private UserInfoModel newUserInfoModel;
    private View parentLayout;
    //private int g = 0;
    ProgressDialog progressDialog;

    /*private String USER_NATIONAL_CARD = "UserNationalCard";
    private String LICENSE_CARD = "LicenseCard";
    private String CAR_PIC = "CarPic";
    private String CAR_BACK_PIC = "CarBackPic";
    private String CAR_IMAGE = "CarImage";*/
    private String SAVE_IMAGE_CODE = "SaveImageCode";


    @Inject
    protected BootstrapServiceProvider serviceProvider;
    @Inject
    UserInfoService userInfoService;
    @Inject
    UserData userData;
    @Inject
    UserImageService userImageService;


    @Bind(R.id.user_national_card_iv)
    protected ImageView userNationalCardIv;

    @Bind(R.id.license_card_iv)
    protected ImageView licenseCardIv;

    @Bind(R.id.car_pic_iv)
    protected ImageView carPicIv;

    @Bind(R.id.car_back_pic_iv)
    protected ImageView carBackPicIv;

    @Bind(R.id.image_car_iv)
    protected ImageView imageCarIv;


    @Bind(R.id.user_national_card_tv)
    protected TextView userNationalCardTv;

    @Bind(R.id.license_card_tv)
    protected TextView licenseCardTv;

    @Bind(R.id.car_pic_tv)
    protected TextView carPicTv;

    @Bind(R.id.car_back_pic_tv)
    protected TextView carBackPicTv;

    @Bind(R.id.image_car_tv)
    protected TextView imageCarTv;


    @Bind(R.id.user_national_card_pb)
    protected ProgressBar userNationalCardPB;

    @Bind(R.id.license_card_pb)
    protected ProgressBar licenseCardPB;

    @Bind(R.id.car_pic_pb)
    protected ProgressBar carPicPB;

    @Bind(R.id.car_back_pic_pb)
    protected ProgressBar carBackPicPB;

    @Bind(R.id.image_car_pb)
    protected ProgressBar imageCarPB;


    @Bind(R.id.user_national_card_description_tv)
    protected TextView userNationalCardDescriptionTv;

    @Bind(R.id.license_card_description_tv)
    protected TextView licenseCardDescriptionTv;

    @Bind(R.id.car_pic_description_tv)
    protected TextView carPicDescriptionTv;

    @Bind(R.id.car_back_pic_description_tv)
    protected TextView carBackPicDescriptionTv;

    @Bind(R.id.image_car_description_tv)
    protected TextView imageCarDescriptionDescriptionTv;


    @Bind(R.id.user_national_card__title_tv)
    protected TextView userNationalCardTitleTv;

    @Bind(R.id.license_card_title_tv)
    protected TextView licenseCardTitleTv;

    @Bind(R.id.car_pic_title_tv)
    protected TextView carPicTitleTv;

    @Bind(R.id.car_back_pic_title_tv)
    protected TextView carBackPicTitleTv;

    @Bind(R.id.image_car_title_tv)
    protected TextView imageCarTitleTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_documents_upload_activity);
        BootstrapApplication.component().inject(this);

        if (getWindow().getDecorView().getLayoutDirection() == View.LAYOUT_DIRECTION_LTR) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.upload_documents_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.mibarim.driver", Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(SAVE_IMAGE_CODE, 0).apply();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getText(R.string.please_wait));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

//        sharedPreferences.edit().putInt(USER_NATIONAL_CARD, 2).apply();

        /*sharedPreferences.edit().putInt(USER_NATIONAL_CARD,2).apply();
        sharedPreferences.edit().putInt(LICENSE_CARD,2).apply();
        sharedPreferences.edit().putInt(CAR_PIC,4).apply();
        sharedPreferences.edit().putInt(CAR_BACK_PIC,5).apply();
        sharedPreferences.edit().putInt(CAR_BACK_PIC,8).apply();*/


        /*userNationalCardPB.setVisibility(View.INVISIBLE);
        licenseCardPB.setVisibility(View.INVISIBLE);
        carPicPB.setVisibility(View.INVISIBLE);
        carBackPicPB.setVisibility(View.INVISIBLE);
        imageCarPB.setVisibility(View.INVISIBLE);*/

        makeAllProgressBarsInvisible();


        userNationalCardDescriptionTv.setVisibility(View.GONE);
        licenseCardDescriptionTv.setVisibility(View.GONE);
        carPicDescriptionTv.setVisibility(View.GONE);
        carBackPicDescriptionTv.setVisibility(View.GONE);
        imageCarDescriptionDescriptionTv.setVisibility(View.GONE);

//        userNationalCardDescriptionTv.setVisibility(View.GONE);


        if (getIntent() != null && getIntent().getExtras() != null) {
            authToken = getIntent().getExtras().getString(Constants.Auth.AUTH_TOKEN);
        }

        getUserInfoFromServer();
        progressDialog.show();


        userNationalCardIv.setOnClickListener(this);
        licenseCardIv.setOnClickListener(this);
        carPicIv.setOnClickListener(this);
        carBackPicIv.setOnClickListener(this);
        imageCarIv.setOnClickListener(this);

        userNationalCardTv.setOnClickListener(this);
        licenseCardTv.setOnClickListener(this);
        carPicTv.setOnClickListener(this);
        carBackPicTv.setOnClickListener(this);
        imageCarTv.setOnClickListener(this);

        userNationalCardTitleTv.setOnClickListener(this);
        licenseCardTitleTv.setOnClickListener(this);
        carPicTitleTv.setOnClickListener(this);
        carBackPicTitleTv.setOnClickListener(this);
        imageCarTitleTv.setOnClickListener(this);


    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.mibarim.driver", Context.MODE_PRIVATE);


        if (v.getId() == R.id.user_national_card_tv | v.getId() == R.id.user_national_card_iv | v.getId() == R.id.user_national_card__title_tv) {
            sharedPreferences.edit().putInt(SAVE_IMAGE_CODE, ImageTypes.UserNationalCard.toInt()).apply();
            //g = ImageTypes.UserNationalCard.toInt();
            selectImage();
        }

        if (v.getId() == R.id.license_card_iv | v.getId() == R.id.license_card_tv | v.getId() == R.id.license_card_title_tv) {
            //g = ImageTypes.LicensePic.toInt();
            sharedPreferences.edit().putInt(SAVE_IMAGE_CODE, ImageTypes.LicensePic.toInt()).apply();
            selectImage();
        }

        if (v.getId() == R.id.car_pic_iv | v.getId() == R.id.car_pic_tv | v.getId() == R.id.car_pic_title_tv) {
            sharedPreferences.edit().putInt(SAVE_IMAGE_CODE, ImageTypes.CarCardPic.toInt()).apply();
            //g = ImageTypes.CarCardPic.toInt();
            selectImage();
        }

        if (v.getId() == R.id.car_back_pic_iv | v.getId() == R.id.car_back_pic_tv | v.getId() == R.id.car_back_pic_title_tv) {
            //g = ImageTypes.CarBckPic.toInt();
            sharedPreferences.edit().putInt(SAVE_IMAGE_CODE, ImageTypes.CarBckPic.toInt()).apply();
            selectImage();
        }

        if (v.getId() == R.id.image_car_iv | v.getId() == R.id.image_car_tv | v.getId() == R.id.image_car_title_tv) {
            //g = ImageTypes.CarImage.toInt();
            sharedPreferences.edit().putInt(SAVE_IMAGE_CODE, ImageTypes.CarImage.toInt()).apply();
            selectImage();
        }

/*
        switch (v.getId()) {
            case R.id.user_national_card_iv:
//                Dialog cameraOrGalleryDialog = new Dialog(this);
//                Intent pickIntent = new Intent(Intent.ACTION_PICK);
//                startActivity(pickIntent);

//                AlertDialog.Builder getImageFrom = new AlertDialog.Builder(this);
//                getImageFrom.setTitle("Select:");
//                final CharSequence[] opsChars = {"take pic", "gallery"};
//                g = ImageTypes.UserNationalCard.ordinal();
//                g = ImageTypes.UserNationalCard.toInt();
                g = 2;
                selectImage();
                *//*getImageFrom.setItems(opsChars, new android.content.DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                            startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
                            startActivity(cameraIntent);
                        }else
                        if(which == 1){
                            Intent intent = new Intent();
                            intent.setType("image*//**//*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
//                            startActivityForResult(Intent.createChooser(intent,getResources().getString(R.string.pickgallery)), SELECT_PICTURE);
                            startActivity(intent);
                        }
                        dialog.dismiss();
                    }
                });*//*
//                cameraOrGalleryDialog.show();
                break;

            case R.id.license_card_iv:
//                g = ImageTypes.LicensePic.ordinal();
//                g = ImageTypes.LicensePic.toInt();
                g = 3;
                selectImage();
                break;
            case R.id.car_pic_iv:
//                g = ImageTypes.CarPic.ordinal();
//                g = ImageTypes.CarPic.toInt();
                g = 4;
                selectImage();
                break;
            case R.id.car_back_pic_iv:
//                g = ImageTypes.CarBckPic.ordinal();
//                g = ImageTypes.CarBckPic.toInt();
                g = 5;
                selectImage();
                break;
            case R.id.image_car_iv:
                g = 8;
                selectImage();
        }*/

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"دوربین", "انتخاب از گالری"};

        AlertDialog.Builder builder = new AlertDialog.Builder(UserDocumentsUploadActivity.this);

//        builder.setTitle("انتخاب عکس");

//        builder.setCustomTitle()
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("دوربین")) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(getExternalCacheDir(), "temp1.jpg");
                    try {
                        f.createNewFile();
                    } catch (IOException e) {
                    }
                    Uri uri = Uri.fromFile(f);

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//                    takePictureIntent.putExtra("value", uri);
                    startActivityForResult(takePictureIntent, REQUEST_CAMERA);

//                    intent.putExtra(IMAGE_TYPE_INT, i);
//                    File f = new File(android.os.Environment
//                            .getExternalStorageDirectory(), "temp.jpg");
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    //startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("انتخاب از گالری")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    intent.setType("image/*");
//                    intent.putExtra(IMAGE_TYPE_INT, 5);
//                    intent.putExtra("someString","thisString");
//                    Bundle b = new Bundle();
//                    b.putInt("gkrg",44);
//                    b.putString("experimentalStringValue","thisIsAString");
//                    startActivityForResult(
//                            Intent.createChooser(intent, "Select File"),
//                            REQUEST_OPEN_GALLERY);
//                    startActivityForResult(intent,REQUEST_OPEN_GALLERY);
                    startActivityForResult(intent, REQUEST_OPEN_GALLERY);
                } /*else if (items[item].equals("انصراف")) {
                    dialog.dismiss();
                }*/
            }
        });
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            Bitmap photo;

//            Intent reciecedIntent = getIntent();
//            int imageTypeInt = reciecedIntent.getIntExtra(IMAGE_TYPE_INT, 0);
            //if (data != null) {

                /*Bundle extras = data.getExtras();
                Bitmap bitmap= extras.getParcelable("data");*/

                Bitmap bitmap = BitmapFactory.decodeFile(getExternalCacheDir() + "/temp1.jpg");

                photo = bitmap;
//                progressBar.setVisibility(View.VISIBLE);


//            final Uri imageUri = data.getData();
//            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            Bitmap bitmap2;
            /*if(data.getData()==null){
                bitmap2 = (Bitmap)data.getExtras().get("data");
            }else{
                try {
                    bitmap2 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }*/

//            doCrop(imageuri);

            SharedPreferences sharedPreferences = this.getSharedPreferences("com.mibarim.driver", Context.MODE_PRIVATE);
            int r = sharedPreferences.getInt(SAVE_IMAGE_CODE, 0);

            switch (r) {
                case 2:
                    userNationalCardPB.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    licenseCardPB.setVisibility(View.VISIBLE);
//                        licenseCardPB.setProgress(0);
//                        licenseCardPB.setProgress(40);

                    break;
                case 4:
                    carPicPB.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    carBackPicPB.setVisibility(View.VISIBLE);
                    break;
                case 8:
                    imageCarPB.setVisibility(View.VISIBLE);
                    break;
            }


//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            saveProfileImage(photo);
            //}

//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//                saveProfileImage(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        }

        if (requestCode == REQUEST_OPEN_GALLERY && resultCode == RESULT_OK) {
            final Uri imageUri = data.getData();
//            Intent reciecedIntent = getIntent();
//            int k = data.getIntExtra(IMAGE_TYPE_INT,0);
//            Bundle extras = data.getExtras();
//            int j = data.getIntExtra(IMAGE_TYPE_INT, 0);
//            String s = data.getStringExtra(IMAGE_TYPE_INT);
//            Bundle extras2 = data.getBundleExtra(IMAGE_TYPE_INT);
//            int imageType = extras.getInt(IMAGE_TYPE_INT, 0);
//            long iSelectedItem = data.getLongExtra(IMAGE_TYPE_INT, 0);
//            doCrop(imageUri);
            SharedPreferences sharedPreferences = this.getSharedPreferences("com.mibarim.driver", Context.MODE_PRIVATE);
            int r = sharedPreferences.getInt(SAVE_IMAGE_CODE, 0);

            try {
                switch (r) {
                    case 2:
                        userNationalCardPB.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        licenseCardPB.setVisibility(View.VISIBLE);
//                        licenseCardPB.setProgress(0);
//                        licenseCardPB.setProgress(40);

                        break;
                    case 4:
                        carPicPB.setVisibility(View.VISIBLE);
                        break;
                    case 5:
                        carBackPicPB.setVisibility(View.VISIBLE);
                        break;
                    case 8:
                        imageCarPB.setVisibility(View.VISIBLE);
                        break;
                }

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                saveProfileImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }


        if (requestCode == CROP_PIC_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
//                Intent receivedIntent = getIntent();
//                int i = receivedIntent.getIntExtra("imageTypeInt", 0);
                Bundle extras = data.getExtras();
                Bitmap bitmap = extras.getParcelable("data");

//                progressBar.setVisibility(View.VISIBLE);
//                progressDialog.show();
//                saveProfileImage(bitmap);
            }
        }


    }

/*

    private void doCrop(Uri picUri) {
        try {

            Intent cropIntent = new Intent("com.android.camera.action.CROP");

            cropIntent.setDataAndType(picUri, "image*/
/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 128);
            cropIntent.putExtra("outputY", 128);
            cropIntent.putExtra("return-data", true);
//            cropIntent.putExtra("imageTypeInt", i);
            startActivityForResult(cropIntent, CROP_PIC_REQUEST_CODE);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
//            String errorMessage = "Whoops - your device doesn't support the crop action!";
            String errorMessage = "دستگاه شما از کراپ عکس پشتیبانی نمی کند.";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
*/


    private void getUserInfoFromServer() {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                /*if (authToken == null) {
                    serviceProvider.invalidateAuthToken();
                    authToken = serviceProvider.getAuthToken(UserDocumentsUploadActivity.this);
                }*/
                userInfoModel = userInfoService.getUserInfo(authToken);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                makeAllProgressBarsInvisible();
                progressDialog.hide();
                Toast.makeText(getBaseContext(), R.string.error_message, Toast.LENGTH_LONG).show();
            }

            @Override
            protected void onSuccess(final Boolean state) throws Exception {
                super.onSuccess(state);
                userData.insertUserInfo(userInfoModel);
//                getImageById(userInfoModel.UserImageId, R.mipmap.ic_camera);
//                setInfoValues(userInfoModel.IsUserRegistered);
                //setEmail();


                SharedPreferences sharedPreferences = UserDocumentsUploadActivity.this.getSharedPreferences("com.mibarim.driver", Context.MODE_PRIVATE);
                int i = sharedPreferences.getInt(SAVE_IMAGE_CODE, -1);
                if (i != 0) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            setTheStates();
                            Toast.makeText(getBaseContext(), "عکس بارگذاری شد.", Toast.LENGTH_LONG).show();
                        }
                    }, 5000);
                }
                else
                    setTheStates();
//                setTheStates();
            }
        }.execute();
    }


    private void setTheStates() {
        switch (userInfoModel.NationalCardImage.getState().toString()) {
            case "NotSent":
                userNationalCardTv.setText(R.string.state_not_sent);
                break;
            case "UnderChecking":
                userNationalCardTv.setText(R.string.state_under_checking);
                userNationalCardIv.setImageResource(R.drawable.ic_cached_black_24dp);
                break;
            case "Accepted":
                userNationalCardTv.setText(R.string.state_accpeted);
                userNationalCardIv.setImageResource(R.drawable.ic_check_circle_black_24dp);
                break;
            case "Rejected":
                userNationalCardDescriptionTv.setVisibility(View.VISIBLE);
                userNationalCardDescriptionTv.setText( userInfoModel.NationalCardImage.getRejectionDescription());
                userNationalCardTv.setText(R.string.state_rejected);
                userNationalCardIv.setImageResource(R.drawable.ic_highlight_off_black_24dp);
                break;
        }


        switch (userInfoModel.LicenseImage.getState().toString()) {
            case "NotSent":
                licenseCardTv.setText(R.string.state_not_sent);
                break;
            case "UnderChecking":
                licenseCardTv.setText(R.string.state_under_checking);
                licenseCardIv.setImageResource(R.drawable.ic_cached_black_24dp);
                break;
            case "Accepted":
                licenseCardTv.setText(R.string.state_accpeted);
                licenseCardIv.setImageResource(R.drawable.ic_check_circle_black_24dp);
                break;
            case "Rejected":
                licenseCardDescriptionTv.setVisibility(View.VISIBLE);
                licenseCardDescriptionTv.setText( userInfoModel.LicenseImage.getRejectionDescription());
                licenseCardTv.setText(R.string.state_rejected);
                licenseCardIv.setImageResource(R.drawable.ic_highlight_off_black_24dp);
                break;
        }

        switch (userInfoModel.CarCardImage.getState().toString()) {
            case "NotSent":
                carPicTv.setText(R.string.state_not_sent);
                break;
            case "UnderChecking":
                carPicTv.setText(R.string.state_under_checking);
                carPicIv.setImageResource(R.drawable.ic_cached_black_24dp);
                break;
            case "Accepted":
                carPicTv.setText(R.string.state_accpeted);
                carPicIv.setImageResource(R.drawable.ic_check_circle_black_24dp);
                break;
            case "Rejected":
                carPicDescriptionTv.setVisibility(View.VISIBLE);
                carPicDescriptionTv.setText(userInfoModel.CarCardImage.getRejectionDescription());
                carPicTv.setText(R.string.state_rejected);
                carPicIv.setImageResource(R.drawable.ic_highlight_off_black_24dp);
                break;
        }

        switch (userInfoModel.CarCardBckImage.getState().toString()) {
            case "NotSent":
                carBackPicTv.setText(R.string.state_not_sent);
                break;
            case "UnderChecking":
                carBackPicTv.setText(R.string.state_under_checking);
                carBackPicIv.setImageResource(R.drawable.ic_cached_black_24dp);
                break;
            case "Accepted":
                carBackPicTv.setText(R.string.state_accpeted);
                carBackPicIv.setImageResource(R.drawable.ic_check_circle_black_24dp);
                break;
            case "Rejected":
                carBackPicDescriptionTv.setVisibility(View.VISIBLE);
                carBackPicDescriptionTv.setText(userInfoModel.CarCardBckImage.getRejectionDescription());
                carBackPicTv.setText(R.string.state_rejected);
                carBackPicIv.setImageResource(R.drawable.ic_highlight_off_black_24dp);
                break;


        }


        switch (userInfoModel.CarImage.getState().toString()) {
            case "NotSent":
                imageCarTv.setText(R.string.state_not_sent);
                break;
            case "UnderChecking":
                imageCarTv.setText(R.string.state_under_checking);
                imageCarIv.setImageResource(R.drawable.ic_cached_black_24dp);
                break;
            case "Accepted":
                imageCarTv.setText(R.string.state_accpeted);
                imageCarIv.setImageResource(R.drawable.ic_check_circle_black_24dp);
                break;
            case "Rejected":
                imageCarDescriptionDescriptionTv.setVisibility(View.VISIBLE);
                imageCarDescriptionDescriptionTv.setText(userInfoModel.CarCardBckImage.getRejectionDescription());
                imageCarTv.setText(R.string.state_rejected);
                imageCarIv.setImageResource(R.drawable.ic_highlight_off_black_24dp);
                break;
        }


        userNationalCardPB.setVisibility(View.INVISIBLE); // or  invisible?
        licenseCardPB.setVisibility(View.INVISIBLE);
        carPicPB.setVisibility(View.INVISIBLE);
        carBackPicPB.setVisibility(View.INVISIBLE);
        imageCarPB.setVisibility(View.INVISIBLE);

        progressDialog.hide();


    }


    private void saveProfileImage(final Bitmap image) {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                /*if (authToken == null) {
                    authToken = serviceProvider.getAuthToken(UserDocumentsUploadActivity.this);
                }*/


                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                Bitmap bitmap = Bitmap.createScaledBitmap(image,820,(int)(820 * ((double)image.getHeight() / (double)image.getWidth())), true);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100 , byteArrayOutputStream);
                String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
//                licenseCardPB.setProgress(60);
                SharedPreferences sharedPreferences = UserDocumentsUploadActivity.this.getSharedPreferences("com.mibarim.driver", Context.MODE_PRIVATE);
                int imageSaveCode = sharedPreferences.getInt(SAVE_IMAGE_CODE, 0);
                response = userImageService.SaveImage(authToken, encodedImage, imageSaveCode);
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
                makeAllProgressBarsInvisible();
                Toast.makeText(UserDocumentsUploadActivity.this,R.string.error_message,Toast.LENGTH_LONG).show();
            }

            @Override
            protected void onSuccess(final Boolean uploadSuccess) throws Exception {
                super.onSuccess(uploadSuccess);
                if (!uploadSuccess) {
                    new HandleApiMessages(UserDocumentsUploadActivity.this, response).showMessages();
                }
                getUserInfoFromServer();
            }
        }.execute();
    }

    private void reloadUserInfo() {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                /*if (authToken == null) {
//                    serviceProvider.invalidateAuthToken();
//                    authToken = serviceProvider.getAuthToken(UserInfoDetailActivity.this);
                }*/
                newUserInfoModel = userInfoService.getUserInfo(authToken);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                makeAllProgressBarsInvisible();
                Toast.makeText(UserDocumentsUploadActivity.this,R.string.error_message,Toast.LENGTH_LONG).show();
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


    public Bitmap getImageById(String imageId, int i) {
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
                getImageFromServer(imageId, i);
            }
        } else {
            getImageFromServer(imageId, i);
        }
        return icon;
    }


    private void getImageFromServer(final String imageId, final int i) {
        new SafeAsyncTask<Boolean>() {
            ImageResponse imageResponse = new ImageResponse();

            @Override
            public Boolean call() throws Exception {
                imageResponse = userInfoService.GetImageById(authToken, imageId);
                if (imageResponse != null && imageResponse.Base64ImageFile != null) {
                    return true;
                }

                return false;

            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                makeAllProgressBarsInvisible();
                Toast.makeText(UserDocumentsUploadActivity.this,R.string.error_message,Toast.LENGTH_LONG).show();
                if (e instanceof android.os.OperationCanceledException) {
                    // User cancelled the authentication process (back button, etc).
                    // Since auth could not take place, lets finish this activity.
//                    finish();
                }
                makeAllProgressBarsInvisible();
            }

            @Override
            protected void onSuccess(final Boolean imageLoaded) throws Exception {
                if (imageLoaded) {
                    byte[] decodedString = Base64.decode(imageResponse.Base64ImageFile, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    String path = ImageUtils.saveImageToInternalStorage(getApplicationContext(), decodedByte, imageResponse.ImageId);
                    userData.insertImage(imageResponse, path);

                    Toast.makeText(getBaseContext(), "عکس بارگذاری شد.", Toast.LENGTH_LONG).show();

                    new HandleApiMessagesBySnackbar(parentLayout, response).showMessages();

                }
            }
        }.execute();
    }

    public void makeAllProgressBarsInvisible() {
        userNationalCardPB.setVisibility(View.INVISIBLE);
        licenseCardPB.setVisibility(View.INVISIBLE);
        carPicPB.setVisibility(View.INVISIBLE);
        carBackPicPB.setVisibility(View.INVISIBLE);
        imageCarPB.setVisibility(View.INVISIBLE);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getText(R.string.please_wait));
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
//        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            public void onCancel(final DialogInterface dialog) {
//            }
//        });
        return dialog;
    }



}
