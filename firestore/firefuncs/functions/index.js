const sendgrid = require('sendgrid');
const uuid = require('uuid');

// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

/**
 * Returns a configured SendGrid client.
 *
 * @param {string} key Your SendGrid API key.
 * @returns {object} SendGrid client.
 */
function getClient (key) {
  if (!key) {
    const error = new Error('SendGrid API key not provided. Make sure you have a "sg_key" property in your request querystring');
    error.code = 401;
    throw error;
  }

  // Using SendGrid's Node.js Library https://github.com/sendgrid/sendgrid-nodejs
  return sendgrid(key);
}

/**
 * Send an email using SendGrid.
 *
 */
exports.sendWelcomeEmail = functions.auth.user().onCreate(event => {
  return Promise.resolve()
    .then(() => {
      const user = event.data; // The Firebase user.

      const email = user.email; // The email of the user.
      const displayName = user.displayName; // The display name of the user.

      console.warn(`Sending email to: ${email} (${displayName})`);
      // Get a SendGrid client and pull the api key out of the config
      const sg = getClient(functions.config().sendgrid.key);

      // Build the SendGrid request to send email
      var helper = require('sendgrid').mail;
      var fromEmail = new helper.Email('customdict@sortedunderbelly.com');
      var toEmail = new helper.Email(email);
      var subject = 'Welcome To Custom Dictionary!';
      var content = new helper.Content('text/plain', 'Thanks for signing up. Please check back often to see new words!');
      var mail = new helper.Mail(fromEmail, subject, toEmail, content);

      const request = sg.emptyRequest({
        method: 'POST',
        path: '/v3/mail/send',
        body: mail.toJSON()
      });

      // Make the request to SendGrid's API
      console.log(`Sending email to: ${email} (${displayName})`);
      return sg.API(request);
    }).then((response) => {
      if (response.statusCode < 200 || response.statusCode >= 400) {
        const error = Error(response.body);
        error.code = response.statusCode;
        throw error;
      }

      console.log(`Email sent!`);
      return 200;
    }).catch((err) => {
      console.error(err);
      const code = err.code || (err.response ? err.response.statusCode : 500) || 500;
      return Promise.reject(err);
    });
});

// exports.sendgridEmail = function sendgridEmail (req, res) {
