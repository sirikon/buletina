const chai = require('chai')
const chaiHttp = require('chai-http')
chai.use(chaiHttp)

const expect = chai.expect
const request = chai.request

describe('Main', () => {
    it('should work', () => {
        expect('patata').to.equal('patata')
    })
    it('should work too', async () => {
        const res = await request('http://example.com').get('/')
        expect(res).to.have.status(200)
        expect(res).to.be.html
    })
})
