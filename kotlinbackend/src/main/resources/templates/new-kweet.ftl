<#import "template.ftl" as layout />

<@layout.mainLayout title="New Post">
<form class="pure-form-stacked" action="/post-new" method="post" enctype="application/x-www-form-urlencoded">
    <input type="hidden" name="date" value="${date}">
    <input type="hidden" name="code" value="${code}">

    <label for="post-text">Text:
        <textarea id="post-text" name="text" rows="5" cols="100"></textarea>
    </label>

    <input class="pure-button pure-button-primary" type="submit" value="Post">

</form>
</@layout.mainLayout>