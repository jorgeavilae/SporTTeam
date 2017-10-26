const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

// show notification in phone when new notifications are stored
exports.notify = functions.database.ref('users/{userId}/notifications/{notificationId}').onWrite(event => {

	// !event.data.val() is a deleted event
	if (!event.data.val()) {
		console.log("Not a new write event");
		return;
	}

	const userID = event.params.userId;
	const notificationRef = event.data.ref.toString();
	const notificationStored = event.data.val();
	console.log(notificationStored);
	console.log(notificationRef);
	console.log(userID);
	/*	s11GNlTT2daaVUI0yqMUtpkY67t2friends {
			checked: true
			data_type: 1
			date: 1501696249065
			extra_data_one: "s11GNlTT2daaVUI0yqMUtpkY67t2"
			message: "You have a new friend!!"
			notification_type: 2
			title: "New friend!!"
		}
	*/
	if(notificationStored.checked) {
		console.log("Already notified");
		return;
	}
	const title = notificationStored.title.toString();
	const message = notificationStored.message.toString();
	const notification_type = notificationStored.notification_type.toString();
	const checked = notificationStored.checked.toString();
	const data_type = notificationStored.data_type.toString();
	const date = notificationStored.date.toString();
	var temp1 = "";
	if (notificationStored.extra_data_one != null)
		temp1 = notificationStored.extra_data_one.toString();
	const extra_data_one = temp1;
	var temp2 = "";
	if (notificationStored.extra_data_two != null)
		temp2 = notificationStored.extra_data_two.toString();
	const extra_data_two = temp2;

	const tokenUserReference = `/tokens/${userID}`;
	const getDeviceTokensPromise = admin.database().ref(tokenUserReference).once('value');

	return Promise.all([getDeviceTokensPromise]).then(results => {
		const tokenSnapshot = results[0];
		const token = tokenSnapshot.val();

		console.log("Promise: text: ", notificationStored.title, "token: ", token);

		// Notification details. Combining 'notification' and 'data' allow int and float in 'data'
		// https://firebase.google.com/docs/cloud-messaging/admin/send-messages?hl=es-419#defining_the_message_payload
		var payload = {
			data: {
				notificationID: notificationRef,
				title: title,
				message: message,
				notification_type: notification_type,
				checked: checked,
				data_type: data_type,
				extra_data_one: extra_data_one,
				extra_data_two: extra_data_two,
				date: date
			}
		};
		return admin.messaging().sendToDevice(token, payload);
	});
});