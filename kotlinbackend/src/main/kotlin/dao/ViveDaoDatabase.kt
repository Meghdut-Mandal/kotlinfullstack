package dao

import Post
import model.Kweet
import model.User
import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.nitrite
import org.joda.time.*
import java.io.*
import kotlin.random.Random

/**
 * A DAO Facade interface for the Database. This allows to provide several implementations.
 *
 * In this case this is used to provide a Database-based implementation using Exposed,
 * and a cache implementation composing another another DAOFacade.
 */
interface ViveDao : Closeable {
    /*
     get the notices starting at the following index
     */
    fun getNotices(start: Int, limit: Int): List<Post>

    fun insertNotice(post: Post)
    /*
    get the total number of notices
     */
    fun getNoticeCount(): Int


    /**
     * Initializes all the required data.
     * In this case this should initialize the Users and Kweets tables.
     */
    fun init()

    /**
     * Counts the number of replies of a kweet identified by its [id].
     */
    fun countReplies(id: Int): Int

    /**
     * Creates a Kweet from a specific [user] name, the kweet [text] content,
     * an optional [replyTo] id of the parent kweet, and a [date] that would default to the current time.
     */
    fun createKweet(user: String, text: String, replyTo: Int? = null, date: DateTime = DateTime.now()): Int

    /**
     * Deletes a kweet from its [id].
     */
    fun deleteKweet(id: Int)

    /**
     * Get the DAO object representation of a kweet based from its [id].
     */
    fun getKweet(id: Int): Kweet

    /**
     * Obtains a list of integral ids of kweets from a specific user identified by its [userId].
     */
    fun userKweets(userId: String): List<Int>

    /**
     * Tries to get an user from its [userId] and optionally its password [hash].
     * If the [hash] is specified, the password [hash] must match, or the function will return null.
     * If no [hash] is specified, it will return the [User] if exists, or null otherwise.
     */
    fun user(userId: String, hash: String? = null): User?

    /**
     * Tries to get an user from its [email].
     *
     * Returns null if no user has this [email] associated.
     */
    fun userByEmail(email: String): User?

    /**
     * Creates a new [user] in the database from its object [User] representation.
     */
    fun createUser(user: User)

    /**
     * Returns a list of Kweet ids, with the ones with most replies first.
     */
    fun top(count: Int = 10): List<Int>

    /**
     * Returns a list of Keet ids, with the recent ones first.
     */
    fun latest(count: Int = 10): List<Int>
}


class DAONitrateDataBase(val dbFile: File) : ViveDao {
    val db = nitrite { file = dbFile }
    val tweetRepo = db.getRepository(Kweet::class.java)
    val userRepo = db.getRepository(User::class.java)
    val postRepo = db.getRepository(Post::class.java)

    override fun getNotices(start: Int, limit: Int): List<Post> {
        return postRepo.find().drop(start).subList(0, limit)
    }

    override fun insertNotice(post: Post) {

        postRepo.insert(post.copy(id=System.currentTimeMillis()))
    }

    override fun getNoticeCount(): Int {
        return postRepo.find().count()
    }

    override fun init() {
    }

    override fun countReplies(id: Int): Int {
        return tweetRepo.find().filter { it.replyTo == id }.count()
    }

    override fun createKweet(user: String, text: String, replyTo: Int?, date: DateTime): Int {
        val id = Random.nextInt(0, 30000)
        tweetRepo.insert(Kweet(id, user, text, (date).toString(), replyTo))
        db.commit()
        return id;
    }

    override fun deleteKweet(id: Int) {
        tweetRepo.remove(Kweet::id eq id)
    }

    override fun getKweet(id: Int): Kweet {
        return tweetRepo.find(Kweet::id eq id).firstOrDefault()
    }

    override fun userKweets(userId: String): List<Int> {
        return tweetRepo.find().filter { it.userId == userId }.map { it.id }.toList()
    }

    override fun user(userId: String, hash: String?): User? {
        return userRepo.find(User::userId eq userId).firstOrDefault()
    }

    override fun userByEmail(email: String): User? {
        return userRepo.find().first { it.email == email }
    }

    override fun createUser(user: User) {
        userRepo.insert(user)
        db.commit()
    }

    override fun top(count: Int): List<Int> {
        return tweetRepo.find().map { it.id }.toList()
    }

    override fun latest(count: Int): List<Int> {
        return top(10)
    }

    override fun close() {
        db.commit()
        db.close()
    }
}