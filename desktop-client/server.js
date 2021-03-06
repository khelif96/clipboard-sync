let clipboardy;
const inquirer = require("./utils/inquirer");
const clipboard = require("./utils/clipboard");
const auth = require("./utils/auth");
const clear = require("clear");
const chalk = require("chalk");
const figlet = require("figlet");
// try {
// } catch (e) {
//   console.log("Error loading dependencies");
//   console.log("Did you run yarn install?");
//   process.exit(1);
// }

let credentials;
const authenticate = async () => {
  const authType = await inquirer.askAuthType();
  credentials = await inquirer.askCreateAccount();
  if (authType.authType == "Yes Sign me in") {
    console.log("Attempting to sign you in");
    let status = await auth.loginUser(credentials);
    // console.log("status", status);
    if (status) {
      // console.log(auth.getClient());
      start(auth.getClient());
    } else {
      authenticate();
    }
  } else if (authType.authType == "No lets create one") {
    auth.registerUser(credentials);
  } else {
    auth.resendValidationEmail(credentials);
  }
};
console.log(chalk.green(figlet.textSync("Clipboard Sync")));

authenticate();

const start = client => {
  clear();
  console.log("Signed In Tracking clipboard data");
  console.log("---------------------------------");
  if (credentials) {
    // console.log(credentials);
    clipboard.init(client);
  }
};
