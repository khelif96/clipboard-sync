const notifier = require("node-notifier");
const clipboardy = require("clipboardy");

let previousClip = "";
let stitchClient;
module.exports = {
  init: client => {
    stitchClient = client;
    setInterval(function() {
      compareClip();
    }, 1000);
  }
};
const compareClip = () => {
  const currentClip = clipboardy.readSync();

  if (currentClip !== previousClip) {
    console.log(currentClip);
    previousClip = currentClip;
    stitchClient.callFunction("syncClipboard", [currentClip]).then(result => {
      notifier.notify({
        title: "Clipboard Sync",
        message: "Copied New Item to clipboard"
      });
    });
  }
};
