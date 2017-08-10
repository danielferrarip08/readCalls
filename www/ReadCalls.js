var exec = require('cordova/exec');

var ReadCalls = {
    lastCall: function (success, error) {
        exec(success, error, "ReadCalls", "getLastCall", []);
    },

    allCalls: function (arg0, success, error) {
        exec(success, error, "ReadCalls", "getAllCalss", []);
    }
}

module.exports = ReadCalls;