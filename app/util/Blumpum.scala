package util

import org.jsoup.Jsoup

import scalaj.http.{Base64, Http, HttpRequest, HttpResponse}


object Constants {
  val BaseApiUrl = "https://api.pinboard.in/v1"
  val DefaultNumberOfPosts = 10
  val UntitledDescriptions = Set("", "untitled")
}

case class Bookmark(url: String, title: String, tags: Array[String]) {
  override def toString: String = s"title: $title\nurl: $url\ntags: ${tags.mkString(",")}"
}

object Bookmark {
  type Post = xml.Node

  def getFirstAttributeAsString(node: Post, attribute: String): String = {
    node.attribute(attribute).get.head.toString
  }

  def getTitle(node: Post): String = getFirstAttributeAsString(node, "description")

  def getUrl(node: Post): String = getFirstAttributeAsString(node, "href")

  def getTags(node: Post): Array[String] = {
    getFirstAttributeAsString(node, "tag").split(" ").filterNot(tag => tag.isEmpty)
  }

  def getBookmarkFromPost(post: Post): Bookmark = {
    val title = Bookmark.getTitle(post)
    val url = Bookmark.getUrl(post)
    val tags = Bookmark.getTags(post)

    Bookmark(url, title, tags)
  }
}

object Blumpum extends App {
  def buildBasicAuthHeader(username:String, password:String): (String, String) = {
    val usernamePasswordEncoded = Base64.encodeString(s"$username:$password")
    ("Authorization", s"Basic $usernamePasswordEncoded")
  }

  def authenticatedGetRequest(url: String, username:String, password:String): HttpRequest = {
    Http(url).headers(buildBasicAuthHeader(username, password))
  }

  def getPosts(numberOfPosts: Int = 0, tag: String = "", username:String, password:String): Seq[Bookmark] = {
    val response = authenticatedGetRequest(s"${Constants.BaseApiUrl}/posts/all", username, password)
      .param("results", s"$numberOfPosts")
      .param("tag", tag)
      .asString

    if (response.code == 200) {
      val postsXML = xml.XML.loadString(response.body)
      postsXML.child.filter(c => c.attributes.nonEmpty)
        .map(Bookmark.getBookmarkFromPost)
    } else {
      throw new Error(s"Not OK response code: ${response.code}")
    }
  }

  def getPostTitle(postLink: String): String = {
    val document = Jsoup.connect(postLink).get()
    document.select("title").text
  }

  def getUntitledPosts(username:String, password:String): Seq[Bookmark] = {
    getPosts(0, "", username, password).filter(bookmark => Constants.UntitledDescriptions.contains(bookmark.title))
  }

  def updatePost(url: String, description: String, tags: Seq[String], username:String, password:String): HttpResponse[String] = {
    authenticatedGetRequest(s"${Constants.BaseApiUrl}/posts/add", username, password)
      .param("url", url).param("description", description)
      .param("tags", tags.mkString(","))
      .asString
  }

  def suggestTagForPost(bookmark: Bookmark, username:String, password:String): HttpResponse[String] = {
    authenticatedGetRequest(s"${Constants.BaseApiUrl}/posts/suggest", username, password)
      .param("url", bookmark.url).asString
  }

  def filterBookmarksByTag(bookmarks: Seq[Bookmark], tag: String): Seq[Bookmark] = {
    bookmarks.filter(bookmark => bookmark.tags.contains(tag))
  }

}
