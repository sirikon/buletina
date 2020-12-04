const chai = require('chai')
const chaiHttp = require('chai-http')
chai.use(chaiHttp)

const expect = chai.expect
const request = chai.request

const config = require('./utils/config')
const smtp = require('./utils/smtp')

const buletinaRequest = () => request(config.buletinaBaseUrl);

const extractConfirmationUrlFromEmail = (emailData) =>
    /confirmation_url\[(.+)\]/.exec(emailData)[1]

it('should return correct page when accessing index', async () => {
    const res = await buletinaRequest().get('/')
    expect(res).to.have.status(200)
    expect(res).to.be.html
    expect(res.text).to.contain('body[index]')
    expect(res.text).to.contain('email[]')
    expect(res.text).to.contain('email_error[]')
})

it('should reply with empty email error given no email when subscribing', async () => {
    const res = await buletinaRequest()
        .post('/subscribe')
        .send('email=')
    expect(res).to.have.status(200)
    expect(res).to.be.html
    expect(res.text).to.contain('body[index]')
    expect(res.text).to.contain('email[]')
    expect(res.text).to.contain('email_error[Email can&#39;t be empty]')
})

it('should reply with incorrect email error given a bad email when subscribing', async () => {
    const res = await buletinaRequest()
        .post('/subscribe')
        .send('email=invalid_email')
    expect(res).to.have.status(200)
    expect(res).to.be.html
    expect(res.text).to.contain('body[index]')
    expect(res.text).to.contain('email[invalid_email]')
    expect(res.text).to.contain('email_error[Email is invalid]')
})

it('should work with complete subscription workflow', async () => {
    await smtp.deleteAllEmails();

    const res = await buletinaRequest()
        .post('/subscribe')
        .send('email=buletina@example.com')
    expect(res).to.have.status(200)
    expect(res).to.be.html
    expect(res.text).to.contain('body[verification_email_sent]')

    const email = await smtp.verifyEmail('buletina@example.com');
    expect(email.rawData).to.contain('body[confirmation_txt]')
    expect(email.rawData).to.contain('body[confirmation_html]')
    await smtp.verifyNoMoreEmails();

    const confirmationUrl = extractConfirmationUrlFromEmail(email.rawData);
    
    const confirmationRes = await request(confirmationUrl).get('')
    expect(confirmationRes).to.have.status(200)
    expect(confirmationRes).to.be.html
    expect(confirmationRes.text).to.contain('body[subscription_confirmed]')
})
