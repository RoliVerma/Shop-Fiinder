package com.codepth.maps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SellerRegistration extends AppCompatActivity {
    private Button otp,signup;
    private EditText num,enterotp;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeras_seller);
        otp=findViewById(R.id.btn_get_otp);
        signup=findViewById(R.id.btn_singup);
        num=findViewById(R.id.et_phone_num);
        enterotp=findViewById(R.id.et_otp);

        mAuth = FirebaseAuth.getInstance();
        loadingbar = new ProgressDialog(this);
        otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phnno=num.getText().toString();
                StringBuilder s=new StringBuilder("+91");
                s.append(phnno);
                Toast.makeText(SellerRegistration.this,String.valueOf(s),Toast.LENGTH_LONG).show();
                loadingbar.setTitle("Phone Verification");
                loadingbar.setMessage("Please wait,while we authenticate your phone");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        String.valueOf(s),        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        SellerRegistration.this,               // Activity (for callback binding)
                        callbacks);        // OnVerificationStateChangedCallbacks


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

                Toast.makeText(SellerRegistration.this, "Invalid please enter correct phone number with your country code", Toast.LENGTH_LONG).show();

            }

            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                loadingbar.dismiss();
                Toast.makeText(SellerRegistration.this, "Code sent", Toast.LENGTH_LONG).show();
                num.setVisibility(View.INVISIBLE);


            }

        };
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = otp.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(SellerRegistration.this, "Please enter the code", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(SellerRegistration.this, "Signup Successfull", Toast.LENGTH_LONG).show();
                            sendtoprofilecreation();


                        } else {
                            String msg = task.getException().toString();
                            Toast.makeText(SellerRegistration.this, msg, Toast.LENGTH_LONG).show();


                        }

                    }
                });
    }


    private void  sendtoprofilecreation() {
        Intent profileintent = new Intent(SellerRegistration.this, SellerProfileCreation.class);
        profileintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(profileintent);
        finish();
    }
}