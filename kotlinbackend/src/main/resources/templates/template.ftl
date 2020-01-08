<#-- @ftlvariable name="user" type="model.User" -->

<#macro mainLayout title="Welcome">
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">



    <title>${title} | Posts</title>
    <link rel="stylesheet" type="text/css" href="style.css">


</head>
<body>
<div class="pure-g">
    <div class="sidebar pure-u-1 pure-u-md-1-4">
        <div class="header">
            <div class="brand-title">Vive Edusoft</div>
            <nav class="nav">
                <ul class="nav-list">
                    <li class="nav-item"><a class="pure-button" href="/">homepage</a></li>
                    <#if user??>
                        <li class="nav-item"><a class="pure-button" href="/user/${user.userId}">my timeline</a></li>
                        <li class="nav-item"><a class="pure-button" href="/post-new">New Post</a></li>
                        <li class="nav-item"><a class="pure-button" href="/logout">sign out
                            [${user.displayName?has_content?then(user.displayName, user.userId)}]</a></li>
                    <#else>
                        <li class="nav-item"><a class="pure-button" href="/register">sign up</a></li>
                        <li class="nav-item"><a class="pure-button" href="/login">sign in</a></li>
                    </#if>
                </ul>
            </nav>
        </div>
    </div>

    <div class="content pure-u-1 pure-u-md-3-4">
        <h2>${title}</h2>
        <#nested />
    </div>
    <div class="footer">
        Vive Edusoft, ${.now?string("yyyy")}
    </div>
</div>
</body>
</html>
</#macro>

<#macro kweet_li kweet>
<#-- @ftlvariable name="kweet" type="model.Kweet" -->
<section class="post">
    <header class="post-header">
        <p class="post-meta">
            <a href="/view_kweet/${kweet.id}">${kweet.date}</a>
            by <a href="/user/${kweet.userId}">${kweet.userId}</a></p>
    </header>
    <div class="post-description">${kweet.text}</div>
</section>
</#macro>

<#macro kweets_list kweets>
<ul>
    <#list kweets as kweet>
        <@kweet_li kweet=kweet></@kweet_li>
    <#else>
        <li>There are no Posts yet</li>
    </#list>
</ul>
</#macro>