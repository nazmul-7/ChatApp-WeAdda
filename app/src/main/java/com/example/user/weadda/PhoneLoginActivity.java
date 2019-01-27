package com.example.user.weadda;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

public class PhoneLoginActivity extends AppCompatActivity {

    private Button SendVerificationCodeButton,VerifyButton;
    private EditText InputPhoneNumber,InputVerifiactionCode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        mAuth= FirebaseAuth.getInstance();

        loadingBar = new ProgressDialog(this);
        SendVerificationCodeButton = findViewById(R.id.send_var_code_button);
        VerifyButton = findViewById(R.id.verify_button);
        InputPhoneNumber = findViewById(R.id.phone_number_input);
        InputVerifiactionCode = findViewById(R.id.verification_code_input);


        SendVerificationCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {



                String phoneNumber = InputPhoneNumber.getText().toString();
                 if(TextUtils.isEmpty(phoneNumber))

                 {
                     Toast.makeText(PhoneLoginActivity.this, "Please enter your phone number first..", Toast.LENGTH_SHORT).show();

                     }
                     else{
                     loadingBar.setTitle(" Verification Code");
                     loadingBar.setMessage("please wait, while we are verifying verification code");
                     loadingBar.setCanceledOnTouchOutside(false);
                      loadingBar.show();

                     PhoneAuthProvider.getInstance().verifyPhoneNumber(
                             phoneNumber,        // Phone number to verify
                             60,                 // Timeout duration
                             TimeUnit.SECONDS,   // Unit of timeout
                             PhoneLoginActivity.this,               // Activity (for callback binding)
                             callbacks);        // OnVerificationStateChangedCallbacks
                 }



            }
        });


        VerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendVerificationCodeButton.setVisibility(view.INVISIBLE);
                InputPhoneNumber.setVisibility(view.INVISIBLE);


                String verificationcode = InputVerifiactionCode.getText().toString();

                if (TextUtils.isEmpty(verificationcode))

                {
                    Toast.makeText(PhoneLoginActivity.this, "Please write verification code first.", Toast.LENGTH_SHORT).show();
                }
                else

                    {  loadingBar.setTitle("Phone Verification");
                        loadingBar.setMessage("please wait, while we are authenticating your phone number");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();



                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationcode);
                         signInWithPhoneAuthCredential(credential);


                    }
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
            {
                 signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e)

            {
                loadingBar.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "Invalid phone number,Please enter valid phone number with your country code..", Toast.LENGTH_SHORT).show();
                SendVerificationCodeButton.setVisibility(View.VISIBLE);
                InputPhoneNumber.setVisibility(View.VISIBLE);


                VerifyButton.setVisibility(View.INVISIBLE);
                InputVerifiactionCode.setVisibility(View.INVISIBLE);



            }

            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {


                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                Toast.makeText(PhoneLoginActivity.this, "", Toast.LENGTH_SHORT).show();
                SendVerificationCodeButton.setVisibility(View.INVISIBLE);
                InputPhoneNumber.setVisibility(View.INVISIBLE);
                loadingBar.dismiss();

                VerifyButton.setVisibility(View.VISIBLE);
                InputVerifiactionCode.setVisibility(View.VISIBLE);

            }

        };

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())

                        {
                            loadingBar.dismiss();
                            Toast.makeText(PhoneLoginActivity.this, "Congratulations, You are logged in successfully..", Toast.LENGTH_SHORT).show();
                            SendUserToMainActivity();

                        }

                        else {
                            String message= task.getException().toString();
                            Toast.makeText(PhoneLoginActivity.this, "Error" +message, Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void SendUserToMainActivity() {


        Intent mainIntent = new Intent(PhoneLoginActivity.this,MainActivity.class);
        startActivity(mainIntent);
        finish();

    }
}
