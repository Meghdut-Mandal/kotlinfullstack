package dao

import Post
import model.Notice
import model.User
import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.nitrite
import java.io.Closeable
import java.io.File
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

    fun insertNotice(post: Post): Long
    /*
    get the total number of notices
     */
    fun getNoticeCount(): Int


    fun getStudentOf(teacherId: String): List<User>

    /**
     * Initializes all the required data.
     * In this case this should initialize the Users and Kweets tables.
     */
    fun init()


    /**
     * Creates a Kweet from a specific [user] name, the kweet [text] content,
     * an optional [replyTo] id of the parent kweet, and a [date] that would default to the current time.
     */
    fun createKweet(notice: Notice): Long

    /**
     * Deletes a kweet from its [id].
     */
    fun deleteKweet(id: Int)

    /**
     * Get the DAO object representation of a kweet based from its [id].
     */
    fun getKweet(id: Long): Notice?

    /**
     * Obtains a list of integral ids of kweets from a specific user identified by its [userId].
     */
    fun userKweets(userId: String): List<Long>

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
    fun top(count: Int = 10): List<Long>

    /**
     * Returns a list of Keet ids, with the recent ones first.
     */
    fun latest(count: Int = 10): List<Long>

    fun resetData()
}


class DAONitrateDataBase(val dbFile: File) : ViveDao {
    val db = nitrite { file = dbFile }
    val tweetRepo = db.getRepository(Notice::class.java)
    val userRepo = db.getRepository(User::class.java)
    val postRepo = db.getRepository(Post::class.java)


    override fun getNotices(start: Int, limit: Int): List<Post> {
        val noticeCount = getNoticeCount()
        return when {
            noticeCount < start -> arrayListOf()
            noticeCount < start + limit -> postRepo.find().drop(start).subList(0, noticeCount - start)
            else -> postRepo.find().drop(start).subList(0, limit)
        }

    }

    override fun insertNotice(post: Post): Long {
        val id = System.nanoTime()
        postRepo.insert(post.copy(id = id))
        return id
    }

    override fun getNoticeCount(): Int {
        return postRepo.find().idSet().size
    }

    override fun getStudentOf(teacherId: String): List<User> {
        return userRepo.find().toList() ?: arrayListOf()
    }

    override fun init() {
    }


    override fun createKweet(notice: Notice): Long {
        val id = Random.nextLong(0, 30000)
        tweetRepo.insert(notice)
        db.commit()
        return id
    }

    override fun deleteKweet(id: Int) {
        tweetRepo.remove(Notice::id eq id)
    }

    override fun getKweet(id: Long): Notice? {
        return tweetRepo.find(Notice::id eq id).firstOrDefault()
    }

    override fun userKweets(userId: String): List<Long> {
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

    override fun top(count: Int): List<Long> {
        return tweetRepo.find().map { it.id }
    }

    override fun latest(count: Int): List<Long> {
        return top(10)
    }

    override fun resetData() {
        tweetRepo.dropAllIndices()
        userRepo.dropAllIndices()
        postRepo.dropAllIndices()
        db.commit()
    }

    override fun close() {
        db.commit()
        db.close()
    }
}