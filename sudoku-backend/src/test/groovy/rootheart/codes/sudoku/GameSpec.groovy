package rootheart.codes.sudoku

import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import spock.lang.Ignore
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class GameSpec extends Specification {

    @LocalServerPort
    private int port

    def 'Test creating a game and make some moves'() {
        given: 'A rest client talking to the local server'
        def usernameAndPassword = """{"username": "kai", "password": "123"}"""
        def client = new RESTClient("http://localhost:$port", ContentType.JSON)

        when: 'I create a new user'
        def userResponse = client.post(path: '/user/sign-up', body: usernameAndPassword)

        then: 'That was successful'
        userResponse["status"] == 200

        when: 'I login'
        def loginResponse = client.post(path: '/login', body: usernameAndPassword)

        then: 'I received an authorization token'
        loginResponse["headers"]["Authorization"]["value"]

        when: 'I create a new game'
        client.headers = ["Authorization": loginResponse["headers"]["Authorization"]["value"] as String]
        def game = client.post(path: '/game')

        then: 'It has an ID and an empty board'
        game['responseData']['gameId'] != null
        game['responseData']['board'] == "0" * 81

        when: 'I put a one in the bottom right field'
        def gameId = game['responseData']['gameId']
        game = client.put(path: "/game/$gameId/9/9/1")

        then: 'The board has changed accordingly'
        game['responseData']['board'] == ("000000000" * 8) + "000000001"

        when: 'I put a one in the bottom left field'
        game = client.put(path: "/game/$gameId/1/9/1")

        then: 'The board has changed accordingly'
        game['responseData']['board'] == ("000000000" * 8) + "100000001"

        when: 'I put a five in the exact center field'
        game = client.put(path: "/game/$gameId/5/5/5")

        then: 'The board has changed accordingly'
        game['responseData']['board'] == ("000000000" * 4) + "000050000" + ("000000000" * 3) + "100000001"

        when: 'I put a nine in the bottom right field'
        game = client.put(path: "/game/$gameId/9/9/9")

        then: 'The board has changed accordingly'
        game['responseData']['board'] == ("000000000" * 4) + "000050000" + ("000000000" * 3) + "100000009"
    }
}
