package com.mibarim.driver.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mibarim.driver.BootstrapApplication;
import com.mibarim.driver.R;
import com.mibarim.driver.adapters.ContactPassengersAdapter;
import com.mibarim.driver.core.Constants;
import com.mibarim.driver.core.ImageUtils;
//import com.mibarim.driver.interfaces.ContactPassengersOnItemClicked;
import com.mibarim.driver.models.ApiResponse;
import com.mibarim.driver.models.ContactPassengersModel;
import com.mibarim.driver.models.ImageResponse;
import com.mibarim.driver.services.ContactService;
import com.mibarim.driver.services.UserInfoService;
import com.mibarim.driver.ui.BootstrapActivity;
import com.mibarim.driver.util.SafeAsyncTask;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.mibarim.driver.core.Constants.GlobalConstants.TRIP_ID_INTENT_TAG;

/**
 * Created by Alireza on 12/11/2017.
 */

public class ContactPassengersActivity extends BootstrapActivity {

    String authToken;
    String tripId;
    RecyclerView contactPassengersRecyclerview;
    private RecyclerView.LayoutManager mLayoutManager;
    ContactPassengersOnItemClicked contactPassengersOnItemClicked;
    TextView nobodyReservedTextview;

    List<ContactPassengersModel> contactPassengersModelList;
    List<Bitmap> bitmapList;
    List<ImageResponse> imageResponseList;

    @Inject
    ContactService contactService;
    @Inject
    UserInfoService userInfoService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_passengers_activity);

        BootstrapApplication.component().inject(this);

        if (getWindow().getDecorView().getLayoutDirection() == View.LAYOUT_DIRECTION_LTR) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        nobodyReservedTextview = (TextView) findViewById(R.id.nobody_reserved_this_trip);
        contactPassengersRecyclerview = (RecyclerView) findViewById(R.id.contact_passengers_recyclerview);
        mLayoutManager = new LinearLayoutManager(this);
        contactPassengersRecyclerview.setLayoutManager(mLayoutManager);

        bitmapList = new ArrayList<>();


        contactPassengersOnItemClicked = new ContactPassengersOnItemClicked() {

            @Override
            public void onContactPassengersClicked(View view, int position, String tel) {

                Intent intent = new Intent(Intent.ACTION_DIAL);

                String telNumber = tel;
                intent.setData(Uri.parse("tel:" + tel));
                startActivity(intent);

                /*Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+ tel));
                startActivity(callIntent);*/

            }
        };


        if (getIntent() != null && getIntent().getExtras() != null) {
            Intent intent = getIntent();
            authToken = intent.getExtras().getString(Constants.Auth.AUTH_TOKEN);
            long tripIdLong = intent.getLongExtra(TRIP_ID_INTENT_TAG, -10);
            tripId = Long.toString(tripIdLong);
        }


        //tripId = Integer.toString(41151);

        getPassengersInfoFromServer();

    }

/*
    @Override
    public void onContactPassengersClicked(View view, int position) {
        Toast.makeText(this, "hey_that's clicked", Toast.LENGTH_SHORT).show();

    }
*/


    public void getPassengersInfoFromServer() {
        contactPassengersModelList = new ArrayList<>();
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                ApiResponse response = contactService.GetContactPassengers(authToken, tripId);

                //Gson gson = new Gson();
                Gson gson = new GsonBuilder().create();
                for (String json : response.Messages) {
                    contactPassengersModelList.add(gson.fromJson(json, ContactPassengersModel.class));
                }

                return true;


            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                Toast.makeText(ContactPassengersActivity.this, R.string.error_server_connection, Toast.LENGTH_LONG).show();

            }

            @Override
            protected void onSuccess(final Boolean res) throws Exception {
                super.onSuccess(res);
                if (contactPassengersModelList != null && contactPassengersModelList.size() != 0)
                    prepareTheList();
                else
                    nobodyReservedTextview.setVisibility(View.VISIBLE);

            }
        }.execute();
    }

    public void prepareTheList() {
        ContactPassengersAdapter passengersAdapter = new ContactPassengersAdapter(contactPassengersModelList, contactPassengersOnItemClicked, bitmapList);
        contactPassengersRecyclerview.setAdapter(passengersAdapter);
        getAllImages();

    }

    public void getAllImages() {
//        int index = 0;
        getImageFromServer();

    }

    public void getNextImages(int index) {

        getImageFromServer();

    }


    private void getImageFromServer() {
        imageResponseList = new ArrayList<>();
//        progressDialog.show();
        new SafeAsyncTask<Boolean>() {
            ImageResponse imageResponse = new ImageResponse();


            @Override
            public Boolean call() throws Exception {
                //String token = serviceProvider.getAuthToken(UserImageUploadActivity.this);
                for (int i = 0; i < contactPassengersModelList.size(); i++) {
                    imageResponse = userInfoService.GetImageById(authToken, contactPassengersModelList.get(i).getImageId());
                    imageResponseList.add(imageResponse);
                }
                if (imageResponse != null && imageResponse.Base64ImageFile != null) {
                    return true;
                }

                return false;

            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
//                progressDialog.hide();

                Toast.makeText(ContactPassengersActivity.this, R.string.error_message, Toast.LENGTH_LONG).show();
            }

            @Override
            protected void onSuccess(final Boolean imageLoaded) throws Exception {
//                if (imageLoaded) {
//                    progressDialog.hide();
                for (int i = 0; i < imageResponseList.size(); i++) {
                    String base64String = imageResponseList.get(i).Base64ImageFile;
                    if (base64String != null) {
                        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        bitmapList.add(i, decodedByte);
                    }
                    else
                        bitmapList.add(null);

                }

//                    byte[] decodedString = Base64.decode(imageResponse.Base64ImageFile, Base64.DEFAULT);
//                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//                    String path = ImageUtils.saveImageToInternalStorage(getApplicationContext(), decodedByte, imageResponse.ImageId);
//                    bitmapList.add(decodedByte);


                ContactPassengersAdapter passengersAdapter = new ContactPassengersAdapter(contactPassengersModelList, contactPassengersOnItemClicked, bitmapList);
                contactPassengersRecyclerview.setAdapter(passengersAdapter);

//                        int nextIndex = index + 1;
//                        getImageFromServer(contactPassengersModelList.get(nextIndex).getImageId() , nextIndex);
//                        getNextImages(nextIndex);


//                    progressDialog.hide();

//                    Toast.makeText(getBaseContext(), "عکس دریافت شد از سرور!", Toast.LENGTH_LONG).show();

//                    if (i == ratingModelList.size() - 1) {


/*                    if (counter == numberOfNonNullImages)
                        adapter = new RatingAdapter(ratingModelList, RatingActivity.this);
                    listView.setAdapter(adapter);*/
//                    }
//                    listView = (ListView) findViewById(R.id.list);

//                    Toast.makeText(RatingActivity.this, "لیست با موفقیت از سرور دریافت شد!", Toast.LENGTH_LONG).show();
/*

                    hideProgress();
                    if (imageLoaded) {
                        gotoBankPayPage(paymentDetailModel);
                    }
*/

//                    new HandleApiMessagesBySnackbar(parentLayout, response).showMessages();
//                    setImage(imageResponse);
//                }
            }
        }.execute();
    }

    public interface ContactPassengersOnItemClicked {

        public void onContactPassengersClicked(View view, int position, String tel);
    }


}
