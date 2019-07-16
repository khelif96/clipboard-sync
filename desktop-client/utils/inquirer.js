const inquirer = require("inquirer");

module.exports = {
  askCreateAccount: () => {
    const questions = [
      {
        name: "email",
        type: "input",
        message: "Please Enter your email address:",
        validate: function(value) {
          if (value.length) {
            return true;
          } else {
            return "Please Enter your email address:";
          }
        }
      },
      {
        name: "password",
        type: "password",
        message: "Enter your password:",
        validate: function(value) {
          if (value.length) {
            return true;
          } else {
            return "Please enter your password.";
          }
        }
      }
    ];
    return inquirer.prompt(questions);
  },
  askAuthType: () => {
    const questions = [
      {
        name: "authType",
        type: "list",
        message: "Do you have an account?",
        choices: ["Yes Sign me in", "No lets create one", "Reset my password"],
        validate: function(value) {
          return true;
        }
      }
    ];
    return inquirer.prompt(questions);
  }
};
