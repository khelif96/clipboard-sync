const {
  Stitch,
  UserPasswordCredential,
  UserPasswordAuthProviderClient
} = require("mongodb-stitch-server-sdk");
require("dotenv").config();
const client = Stitch.initializeDefaultAppClient(process.env.APP_CLIENT);

module.exports = {
  getClient: () => {
    return client;
  },
  registerUser: credentials => {
    const emailPassClient = Stitch.defaultAppClient.auth.getProviderClient(
      UserPasswordAuthProviderClient.factory
    );
    emailPassClient
      .registerWithEmail(credentials.email, credentials.password)
      .then(() => {
        console.log("Successfully sent account confirmation email!");
        console.log("Validate your email and try again!");
        process.exit(0);
      })
      .catch(err => {
        console.log("Error registering new user:", err);
        process.exit(1);
      });
  },
  loginUser: credentials => {
    const credential = new UserPasswordCredential(
      credentials.email,
      credentials.password
    );
    return (
      client.auth
        .loginWithCredential(credential)
        // Returns a promise that resolves to the authenticated user
        .then(authedUser => {
          console.log("Signed in");
          return true;
        })
        .catch(err => {
          console.log("Error signing in ", err);
          return false;
        })
    );
  },
  resendValidationEmail: credentials => {
    const emailPassClient = Stitch.defaultAppClient.auth.getProviderClient(
      UserPasswordAuthProviderClient.factory
    );
    emailPassClient
      .resendConfirmationEmail(credentials.email)
      .then(() => {
        console.log("Succesfully resent confirmation email");
        console.log("Try again when you validated your email!");
        process.exit(0);
      })
      .catch(err => {
        console.log("Error resending email", err);
        process.exit(1);
      });
  }
};
