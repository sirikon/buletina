import { expect } from 'chai'
import superagent from 'superagent'

import config from './utils/config'
import smtp from './utils/smtp'

const buletinaRequest = (method: 'GET' | 'POST', path: string) => 
    superagent(method, `${config.buletinaBaseUrl}${path}`);

const extractConfirmationUrlFromEmail = (emailData) =>
    /confirmation_url\[(.+)\]/.exec(emailData)[1]

const expectResponse = (res: superagent.Response, status: number, contentType: string) => {
    expect(res.status).to.equal(status)
    expect(res.type).to.equal(contentType)
}

describe('Main behavior', () => {
    beforeEach(async () => await smtp.deleteAllEmails())
    afterEach(async () => await smtp.expectNoMoreEmails())

    it('should return correct page when accessing index', async () => {
        const res = await buletinaRequest('GET', '/')
        expectResponse(res, 200, "text/html")
        expect(res.text).to.contain('body[index]')
        expect(res.text).to.contain('email[]')
        expect(res.text).to.contain('email_error[]')
    })

    it('should reply with empty email error given no email when subscribing', async () => {
        const res = await buletinaRequest('POST', '/subscribe')
            .send('email=')
        expectResponse(res, 200, "text/html")
        expect(res.text).to.contain('body[index]')
        expect(res.text).to.contain('email[]')
        expect(res.text).to.contain('email_error[Email can&#39;t be empty]')
    })

    it('should reply with incorrect email error given a bad email when subscribing', async () => {
        const res = await buletinaRequest('POST', '/subscribe')
            .send('email=invalid_email')
        expectResponse(res, 200, "text/html")
        expect(res.text).to.contain('body[index]')
        expect(res.text).to.contain('email[invalid_email]')
        expect(res.text).to.contain('email_error[Email is invalid]')
    })

    it('should work with complete subscription workflow', async () => {
        const res = await buletinaRequest('POST', '/subscribe')
            .send('email=buletina@example.com')
        expectResponse(res, 200, "text/html")
        expect(res.text).to.contain('body[verification_email_sent]')

        const email = await smtp.expectEmail('buletina@example.com');
        expect(email.rawData).to.contain('body[confirmation_txt]')
        expect(email.rawData).to.contain('body[confirmation_html]')

        const confirmationUrl = extractConfirmationUrlFromEmail(email.rawData);
        
        const confirmationRes = await superagent.get(confirmationUrl)
        expectResponse(confirmationRes, 200, "text/html")
        expect(confirmationRes.text).to.contain('body[subscription_confirmed]')
    })
})
