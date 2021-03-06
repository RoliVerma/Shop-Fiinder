package Seller;

import Adapters.BuyerListAdapter;
import Buyer.AboutUsDialog;
import Common.VerticalSpacingItemDecoration;
import Models.BuyerList;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepth.maps.R;
import com.codepth.maps.SplashActivity;
import com.codepth.maps.Welcomepage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SellerChatActivity extends AppCompatActivity {
    private String uid;
    FirebaseAuth sAuth;
    RecyclerView listOfBuyers;
    ArrayList<String> buyerUid;
    ArrayList<BuyerList> list;
    FirebaseFirestore db;
    FirebaseAuth auth;
    private Toolbar mtoolbar;
    FirebaseFirestore fstore;
    private ProgressBar progressBar;
    TextView msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_chat);
        sAuth=FirebaseAuth.getInstance();
        uid=sAuth.getUid();
        progressBar = findViewById(R.id.spinKit);
        listOfBuyers=findViewById(R.id.listOfBuyers);
        listOfBuyers.setLayoutManager(new LinearLayoutManager(SellerChatActivity.this));
        VerticalSpacingItemDecoration itemDecoration=new VerticalSpacingItemDecoration(20);
        listOfBuyers.addItemDecoration(itemDecoration);
        list=new ArrayList<>();
        buyerUid=new ArrayList<>();
        auth=FirebaseAuth.getInstance();
        mtoolbar = (Toolbar) findViewById(R.id.Seller_toolbar);
        setSupportActionBar(mtoolbar);
        fstore = FirebaseFirestore.getInstance();
        msg=findViewById(R.id.msg);


        db=FirebaseFirestore.getInstance();
        db.collection("Chats").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                buyerUid.clear();
                if(task.isSuccessful()){
                    buyerUid.clear();
                    for(QueryDocumentSnapshot snapshot:task.getResult()){
                        if(snapshot.getData().get("receiver").toString().equals(auth.getUid())
                        &&
                        !buyerUid.contains(snapshot.getData().get("sender").toString())
                        )
                            buyerUid.add(snapshot.getData().get("sender").toString());
                        Toast.makeText(SellerChatActivity.this, ""+buyerUid, Toast.LENGTH_SHORT).show();
                        db.collection("Buyer").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                list.clear();
                                if(task.isSuccessful()){
                                    list.clear();
                                    for(QueryDocumentSnapshot snapshot:task.getResult()){
                                        if(buyerUid.contains(snapshot.getData().get("uid").toString())){
                                            list.add(new BuyerList(snapshot.getData().get("name").toString(),snapshot.getData().get("uid").toString(),snapshot.get("phone").toString()));
                                            Toast.makeText(SellerChatActivity.this, ""+list, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    listOfBuyers.setAdapter(new BuyerListAdapter(SellerChatActivity.this,list));
                                    progressBar.setVisibility(View.INVISIBLE);
                                    if(list.size()==0)
                                        msg.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                         }
                }
            }
        });


        listOfBuyers.setAdapter(new BuyerListAdapter(SellerChatActivity.this,list));


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.mebubar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.main_logout)
        {
            String currentuserid = auth.getCurrentUser().getUid();
            DocumentReference documentReference=fstore.collection("Seller").document(currentuserid);
            documentReference.update("token", "");
            FirebaseAuth.getInstance().signOut();
            sendUsertologinactivity();
            finish();
        }
        if(item.getItemId()==R.id.main_settings)
        {
            sendUsertosettings();


        }
        if(item.getItemId()==R.id.about_us)
        {

            openDialog();
        }

        return true;
    }
    private void openDialog()
    {
        AboutUsDialog aboutUsDialog=new AboutUsDialog();
        aboutUsDialog.show(getSupportFragmentManager(),"About Us Dialog");
    }
    private void sendUsertosettings() {
        startActivity(new Intent(this,SellerProfileCreation.class));

    }

    private void sendUsertologinactivity() {
        startActivity(new Intent(this, Welcomepage.class));
        finish();
    }

//
//    @Override
//    public void onBackPressed() {
//        startActivity(new Intent(this, Welcomepage.class));
//    }

    private String receiveUid() { //gets uid of the clicked marker shop from intentExtra
        Intent intentRecv = getIntent();
        String selleruid = intentRecv.getStringExtra("SellerUid");
        return selleruid;
    }


}
