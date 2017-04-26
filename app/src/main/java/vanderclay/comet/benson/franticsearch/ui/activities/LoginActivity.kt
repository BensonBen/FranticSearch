package vanderclay.comet.benson.franticsearch.ui.activities


import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import vanderclay.comet.benson.franticsearch.R


/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(), View.OnClickListener {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // Reference to the email view
    private var mEmailView: TextView? = null

    //Reference to the password View
    private var mPasswordView: TextView? = null

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

    //Reference to the create account Button
    private var createAccountButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Instantiate a Reference to the Firebase Auth Object
        this.mAuth = FirebaseAuth.getInstance()
        mEmailView = findViewById(R.id.email) as TextView
        mPasswordView = findViewById(R.id.password) as TextView
        signInButton = findViewById(R.id.email_sign_in_button) as Button
        signInButton?.setOnClickListener(this)
        createAccountButton = findViewById(R.id.create_account_button) as Button
        createAccountButton?.setOnClickListener(this)

        Log.w(TAG, "Assigning listener to Firebase Authorize object")
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            var user: FirebaseUser? = firebaseAuth.currentUser
            if (user != null) {
                Log.w(TAG, "user sign in successful")
            } else {
                Log.w(TAG, "User not currently signed in.")
            }
        }
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


    private fun signIn(email: String, password: String) {
        Log.w(TAG, "signIn: " + email)
        val intent = Intent(baseContext, MainActivity::class.java)
        intent.action = "SEARCH_INTENT"
        if(!isValideForm()){
            showSnackBar("Password or Email had Invalid Format")
            return
        }
        signInButton?.isEnabled = false
        mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.w(TAG, "Sign In Complete" + task.isSuccessful)
                showSnackBar("Successfully Signed In")
                startActivity(intent)
                finish()
            } else {
                signInButton?.isEnabled = true
                showSnackBar("Login Unsuccessful")
            }
        }
    }

    private fun transferToCreateAccount(){
        val intent = Intent(this, CreateAccountActivity::class.java)
        super.startActivity(intent)
    }

    private fun showSnackBar(message: String){
        val snackbar = Snackbar.make(LoginActivity, message, Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    private fun isValideForm(): Boolean{
        val emailText = mEmailView?.text.toString()
        val passwordText = mPasswordView?.text.toString()
        if(!isEmailValid(emailText) || !isPasswordValid(passwordText)){
            return false
        }
        return true
    }

    override fun onClick(v: View) {
        val i = v.id
        if (i == R.id.email_sign_in_button) {
            signIn(mEmailView?.text.toString(), mPasswordView?.text.toString())
        }
        else if(i == R.id.create_account_button){
            transferToCreateAccount()
        }
    }

    private fun signOut() {
        mAuth!!.signOut()
    }

    private fun isEmailValid(email: String): Boolean {
        return email.contains("@")
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 6
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

