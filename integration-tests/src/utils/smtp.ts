import config from './config'
import superagent from 'superagent'

async function getEmails(): Promise<Array<any>> {
    const res = await superagent.get(config.smtpBaseUrl + '/api/email');
    return res.body;
}

async function deleteEmail(id: String) {
    await superagent.delete(`${config.smtpBaseUrl}/api/email/${id}`);
}

async function deleteAllEmails() {
    const emails = await getEmails();
    for(const email of emails) {
        await deleteEmail(email.id);
    }
}

async function verifyEmail(address: String) {
    const emails = await getEmails();
    const filteredEmails = emails.filter((email) => email.toAddress === address);
    if (filteredEmails.length === 0) {
        throw new Error("Failed verification. No emails sent to address " + address);
    }
    const selectedEmail = filteredEmails[0];
    await deleteEmail(selectedEmail.id);
    return selectedEmail;
}

async function verifyNoMoreEmails() {
    const emails = await getEmails();
    if (emails.length > 0) {
        throw new Error(`Expected no more emails, but there are ${emails.length} more`);
    }
}

export default {
    getEmails,
    deleteEmail,
    deleteAllEmails,
    verifyEmail,
    verifyNoMoreEmails
}
