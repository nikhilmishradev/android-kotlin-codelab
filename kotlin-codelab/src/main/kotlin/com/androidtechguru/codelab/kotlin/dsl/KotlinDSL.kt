package com.androidtechguru.codelab.kotlin.dsl

fun main() {
    println("╔══════════════════════════════════════════╗")
    println("║    KOTLIN DSL — Codelab                  ║")
    println("╚══════════════════════════════════════════╝\n")

    // ─────────────────────────────────────────
    // 1. LAMBDA WITH RECEIVER
    // ─────────────────────────────────────────
    println("=== 1. Lambda with Receiver ===")

    // Normal lambda: (StringBuilder) -> Unit
    // Lambda with receiver: StringBuilder.() -> Unit
    // Inside the lambda, 'this' re
    // fers to the receiver (StringBuilder)

    fun buildString(action: StringBuilder.() -> Unit): String {
        val sb = StringBuilder()
        sb.action()  // or action(sb)
        return sb.toString()
    }

    val result = buildString {
        append("Hello")     // 'this' is StringBuilder
        append(", ")
        append("World!")
    }
    println("  buildString: $result")

    // This is EXACTLY how Kotlin's buildString, apply, and run work!
    // apply = fun <T> T.apply(block: T.() -> Unit): T { block(); return this }

    // ─────────────────────────────────────────
    // 2. TYPE-SAFE BUILDER — HTML DSL
    // ─────────────────────────────────────────
    println("\n=== 2. Type-Safe Builder (HTML DSL) ===")

    val html = html {
        head {
            title("Kotlin Codelab")
        }
        body {
            h1("Hello, DSL!")
            p("This is built with a type-safe Kotlin DSL.")
            p("Each function call is a lambda with receiver.")
        }
    }
    println(html)

    // ─────────────────────────────────────────
    // 3. @DslMarker
    // ─────────────────────────────────────────
    println("\n=== 3. @DslMarker ===")
    println("""
    Problem without @DslMarker:
      html {
        body {
          body { }  // Oops! Accidentally accessing outer receiver's body()
        }
      }

    Solution: @DslMarker annotation restricts scope leaking.
    With @HtmlDsl, inner lambdas can only access their own receiver.
    To access outer receiver explicitly: this@html.head { }
    """.trimIndent())

    // ─────────────────────────────────────────
    // 4. CONFIG DSL
    // ─────────────────────────────────────────
    println("\n=== 4. Config DSL ===")

    // Real-world pattern: configuration builders
    val serverConfig = server {
        host = "localhost"
        port = 8080

        database {
            url = "jdbc:postgresql://localhost/mydb"
            username = "admin"
            maxConnections = 10
        }

        logging {
            level = "DEBUG"
            file = "/var/log/app.log"
        }
    }
    println(serverConfig)

    // ─────────────────────────────────────────
    // 5. APPLY AS MINI-DSL
    // ─────────────────────────────────────────
    println("\n=== 5. Apply as Mini-DSL ===")

    // apply is the simplest form of DSL — configure an object
    data class User(
        var name: String = "",
        var age: Int = 0,
        var email: String = "",
        var roles: MutableList<String> = mutableListOf()
    )

    val user = User().apply {
        name = "Alice"
        age = 25
        email = "alice@test.com"
        roles.addAll(listOf("admin", "developer"))
    }
    println("  User: $user")

    // Nested apply — common in Android (e.g., Intent, Bundle, Notification)
    val config = mutableMapOf<String, Any>().apply {
        put("debug", true)
        put("version", "1.0")
        put("features", mutableListOf<String>().apply {
            add("auth")
            add("push")
            add("analytics")
        })
    }
    println("  Config: $config")

    // ─────────────────────────────────────────
    // 6. REAL-WORLD DSL PATTERNS
    // ─────────────────────────────────────────
    println("\n=== 6. Real-World DSL Patterns ===")

    // Pattern 1: Route DSL (inspired by Ktor)
    println("--- Route DSL ---")
    val routes = router {
        get("/users") { "List users" }
        get("/users/{id}") { "Get user" }
        post("/users") { "Create user" }
        delete("/users/{id}") { "Delete user" }
    }
    routes.forEach { println("  ${it.method} ${it.path}") }

    // Pattern 2: Test assertion DSL
    println("\n--- Assertion DSL ---")
    42.should {
        beGreaterThan(0)
        beLessThan(100)
        beEqualTo(42)
    }

    // Pattern 3: Gradle KTS uses the SAME concepts
    println("""

    Gradle KTS example (same DSL patterns):

      dependencies {                           // lambda with receiver on DependencyHandler
        implementation("com.example:lib:1.0")  // function call on receiver
        testImplementation(libs.junit)
      }

      android {                                // lambda with receiver on AndroidExtension
        compileSdk = 35                        // property setter
        defaultConfig {                        // nested lambda with receiver
          minSdk = 26
        }
      }

    Compose example:

      Column(                                  // @Composable function
        modifier = Modifier
          .fillMaxWidth()                      // builder pattern
          .padding(16.dp)
      ) {                                      // trailing lambda (content slot)
        Text("Hello")                          // calls within receiver scope
      }
    """.trimIndent())

    println("\n✅ Kotlin DSL Codelab Complete!")
}

// ═══════════════════════════════════════════
// HTML DSL
// ═══════════════════════════════════════════

@DslMarker
annotation class HtmlDsl

@HtmlDsl
class HTML {
    private val children = mutableListOf<String>()

    fun head(block: Head.() -> Unit) {
        val head = Head().apply(block)
        children.add(head.render())
    }

    fun body(block: Body.() -> Unit) {
        val body = Body().apply(block)
        children.add(body.render())
    }

    fun render(): String = "<html>\n${children.joinToString("\n")}\n</html>"
    override fun toString() = render()
}

@HtmlDsl
class Head {
    private var titleText = ""
    fun title(text: String) { titleText = text }
    fun render() = "  <head><title>$titleText</title></head>"
}

@HtmlDsl
class Body {
    private val elements = mutableListOf<String>()
    fun h1(text: String) { elements.add("    <h1>$text</h1>") }
    fun p(text: String) { elements.add("    <p>$text</p>") }
    fun render() = "  <body>\n${elements.joinToString("\n")}\n  </body>"
}

fun html(block: HTML.() -> Unit): HTML = HTML().apply(block)

// ═══════════════════════════════════════════
// Config DSL
// ═══════════════════════════════════════════

class ServerConfig {
    var host: String = "0.0.0.0"
    var port: Int = 80
    var db: DatabaseConfig? = null
    var log: LoggingConfig? = null

    fun database(block: DatabaseConfig.() -> Unit) {
        db = DatabaseConfig().apply(block)
    }

    fun logging(block: LoggingConfig.() -> Unit) {
        log = LoggingConfig().apply(block)
    }

    override fun toString() = """
    |  ServerConfig:
    |    host=$host, port=$port
    |    database: url=${db?.url}, user=${db?.username}, maxConn=${db?.maxConnections}
    |    logging: level=${log?.level}, file=${log?.file}
    """.trimMargin()
}

class DatabaseConfig {
    var url: String = ""
    var username: String = ""
    var maxConnections: Int = 5
}

class LoggingConfig {
    var level: String = "INFO"
    var file: String = "stdout"
}

fun server(block: ServerConfig.() -> Unit): ServerConfig = ServerConfig().apply(block)

// ═══════════════════════════════════════════
// Route DSL
// ═══════════════════════════════════════════

data class Route(val method: String, val path: String, val handler: () -> String)

class Router {
    private val _routes = mutableListOf<Route>()
    val routes: List<Route> get() = _routes

    fun get(path: String, handler: () -> String) { _routes.add(Route("GET", path, handler)) }
    fun post(path: String, handler: () -> String) { _routes.add(Route("POST", path, handler)) }
    fun delete(path: String, handler: () -> String) { _routes.add(Route("DELETE", path, handler)) }
}

fun router(block: Router.() -> Unit): List<Route> = Router().apply(block).routes

// ═══════════════════════════════════════════
// Assertion DSL
// ═══════════════════════════════════════════

class IntAssertions(private val value: Int) {
    fun beGreaterThan(expected: Int) {
        check(value > expected) { "$value should be > $expected" }
        println("  ✓ $value > $expected")
    }
    fun beLessThan(expected: Int) {
        check(value < expected) { "$value should be < $expected" }
        println("  ✓ $value < $expected")
    }
    fun beEqualTo(expected: Int) {
        check(value == expected) { "$value should == $expected" }
        println("  ✓ $value == $expected")
    }
}

fun Int.should(block: IntAssertions.() -> Unit) {
    IntAssertions(this).block()
}
