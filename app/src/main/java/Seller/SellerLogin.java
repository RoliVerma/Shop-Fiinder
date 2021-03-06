package Seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepth.maps.R;
import com.codepth.maps.SplashActivity;
import com.codepth.maps.Welcomepage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

import static Seller.SellerPhoneAuth.Shared_pref;

//import static com.codepth.maps.SellerPhoneAuth.Shared_pref;

public class SellerLogin extends AppCompatActivity {
    private Button otpBtn, loginBtn;
    private EditText numEt, otpEt;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;
    private FirebaseFirestore fstore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_login);
        otpBtn = findViewById(R.id.btn_get_otp);
        loginBtn = findViewById(R.id.btn_login);
        numEt = findViewById(R.id.et_phone_number);
        otpEt = findViewById(R.id.et_otp);
        fstore = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        loadingbar = new ProgressDialog(this);
        otpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numEt.getText().toString() != null) {

                    String phnno = numEt.getText().toString();
                    StringBuilder s = new StringBuilder("+91");
                    s.append(phnno);
                    //Toast.makeText(PhnRegistration.this,String.valueOf(s),Toast.LENGTH_LONG).show();
                    loadingbar.setTitle("Phone Verification");
                    loadingbar.setMessage("Please wait,while we authenticate your phone");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            String.valueOf(s),        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            SellerLogin.this,               // Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacks

                    otpBtn.setVisibility(View.INVISIBLE);
                    loginBtn.setVisibility(View.VISIBLE);
                    numEt.setVisibility(View.INVISIBLE);
                    otpEt.setVisibility(View.VISIBLE);
                }
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                loadingbar.dismiss();

                Toast.makeText(SellerLogin.this, "Invalid please enter correct phone number"+e, Toast.LENGTH_LONG).show();

            }

            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                loadingbar.dismiss();
                Toast.makeText(SellerLogin.this, "Code sent", Toast.LENGTH_LONG).show();
                numEt.setVisibility(View.INVISIBLE);


            }

        };
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = otpEt.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(SellerLogin.this, "Please enter the code", Toast.LENGTH_LONG).show();
                } else {

                    loadingbar.setTitle("Code Verification");
                    loadingbar.setMessage("Please wait,while we are verifying");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingbar.dismiss();
                            verifyexistence();
                        } else {
                            String msg = task.getException().toString();
                            Toast.makeText(SellerLogin.this, msg, Toast.LENGTH_LONG).show();


                        }

                    }
                });
    }


    private void verifyexistence() {
        final String currentuserid = mAuth.getCurrentUser().getUid();
        DocumentReference documentReference=fstore.collection("Seller").document(currentuserid);
        documentReference.update("token",SplashActivity.token);
        fstore.collection("Seller").document(currentuserid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    SharedPreferences sharedPreferences = getSharedPreferences(Shared_pref, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("role", "0");
                    editor.apply();
                    Toast.makeText(SellerLogin.this, "Welcome Back", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SellerLogin.this, SellerChatActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SellerLogin.this, "No such user exists", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SellerLogin.this, Welcomepage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    startActivity(intent);
                    finish();

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SellerLogin.this, "Failed with Exception", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SellerLogin.this, Welcomepage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                startActivity(intent);
                finish();
            }
        });
    }
}
