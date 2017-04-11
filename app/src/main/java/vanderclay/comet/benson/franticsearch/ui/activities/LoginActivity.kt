package vanderclay.comet.benson.franticsearch.ui.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.app.LoaderManager.LoaderCallbacks

import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask

import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.inputmethod.EditorInfo
import java.util.ArrayList
import vanderclay.comet.benson.franticsearch.R
import android.Manifest.permission.READ_CONTACTS
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.GoogleAuthProvider;
import kotlinx.android.synthetic.main.activity_login.*


/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(), LoaderCallbacks<Cursor>, View.OnClickListener {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // Reference to the email view
    private var mEmailView: AutoCompleteTextView? = null

    //Reference to the password View
    private var mPasswordView: EditText? = null

    //Reference to the Progress View
    private var mProgressView: View? = null

    //Reference to the login view
    private var mLoginFormView: View? = null

    // Reference to the firebase authorization object
    private var mAuth: FirebaseAuth? = null

    // Reference to the Sign in Button
    private var signInButton: Button? = null

    // Reference to the firebase Auth State Changed listener
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null

    //The tag filter
    private val TAG: String = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Instantiate a Reference to the Firebase Auth Object
        this.mAuth = FirebaseAuth.getInstance()
        mEmailView = findViewById(R.id.email) as AutoCompleteTextView
        mPasswordView = findViewById(R.id.password) as EditText
        signInButton = findViewById(R.id.email_sign_in_button) as Button
        mLoginFormView = findViewById(R.id.login_form)
        mProgressView = findViewById(R.id.login_progress)

        signInButton?.setOnClickListener(this)

        Log.w(TAG, "Assigning listener to Firebase Authorize object")
//        object : KeyAdapter() {
            mAuthListener = object: FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {

                var user: FirebaseUser? = firebaseAuth.currentUser
                if (user != null) {
                    Log.w(TAG, "user sign in successful")
                } else {
                   Log.w(TAG, "User not currently signed in.")
                }

            }
        }
    }

    /*
     * user can press create an account.
     */
    private fun createAccount(email: String , password: String) {
        Log.w(TAG, "createAccount: " + email)
//        if (!validateForm()) {
//            return;
//        }
//
//        showProgressDialog();
//
//        // [START create_user_with_email]
        mAuth?.createUserWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(this, OnCompleteListener {
                    @Override
                    fun onComplete(task: Task<AuthResult>){
                        Log.w(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful())
                        if(!task.isSuccessful){
                            Toast.makeText(this.applicationContext, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
    }

    public override fun onStart() {
        super.onStart()
        Log.w(TAG, "Application On Create called: Assigning Auth State Listener")
        mAuth!!.addAuthStateListener(this.mAuthListener!!)
    }

    /*
     * Override the onStop method and
     */
    public override fun onStop() {
        Log.w(TAG, "Stopping the task.")
        super.onStop()
        if (mAuthListener != null) {
            Log.w(TAG, "stop listening for changes in the login activity")
            mAuth!!.removeAuthStateListener(mAuthListener!!)
        }
    }

    private fun signIn(email: String, password: String){
        Log.w(TAG, "signIn: " + email)
        //validate the form
        mAuth!!.signInWithEmailAndPassword(email, password)?.addOnCompleteListener(this, object: OnCompleteListener<AuthResult> {
            override fun onComplete(task: Task<AuthResult>){
                Log.w(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful())
                if(!task.isSuccessful){
                    Log.w(TAG, "signInWithEmail:failed", task.getException());
                    Toast.makeText(applicationContext, "Failed to sign in.", Toast.LENGTH_SHORT).show();
                }
            }
        })
    }

    override fun onClick(v: View) {
        val i = v.id

        if (i == R.id.email_sign_in_button) {
            signIn(mEmailView?.getText().toString(), mPasswordView?.getText().toString())
        }
    }

    private fun signOut() {
        mAuth!!.signOut()
//        updateUI(null)
    }


    private fun isEmailValid(email: String): Boolean {
        //TODO: Replace this with your own logic
        return email.contains("@")
    }

    private fun isPasswordValid(password: String): Boolean {
        //TODO: Replace this with your own logic
        return password.length > 4
    }



    override fun onCreateLoader(i: Int, bundle: Bundle): Loader<Cursor> {
        return CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?", arrayOf(ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE),

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC")
    }

    override fun onLoadFinished(cursorLoader: Loader<Cursor>, cursor: Cursor) {
        val emails = ArrayList<String>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS))
            cursor.moveToNext()
        }

        addEmailsToAutoComplete(emails)
    }

    override fun onLoaderReset(cursorLoader: Loader<Cursor>) {

    }

    private fun addEmailsToAutoComplete(emailAddressCollection: List<String>) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        val adapter = ArrayAdapter(this@LoginActivity,
                android.R.layout.simple_dropdown_item_1line, emailAddressCollection)

        mEmailView!!.setAdapter(adapter)
    }

    private interface ProfileQuery {
        companion object {
            val PROJECTION = arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS, ContactsContract.CommonDataKinds.Email.IS_PRIMARY)

            val ADDRESS = 0
            val IS_PRIMARY = 1
        }
    }

    companion object {

        /**
         * Id to identity READ_CONTACTS permission request.
         */
        private val REQUEST_READ_CONTACTS = 0

        /**
         * A dummy authentication store containing known user names and passwords.
         * TODO: remove after connecting to a real authentication system.
         */
        private val DUMMY_CREDENTIALS = arrayOf("foo@example.com:hello", "bar@example.com:world")
    }
}

