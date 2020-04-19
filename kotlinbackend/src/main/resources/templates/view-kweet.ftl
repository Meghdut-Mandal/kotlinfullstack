<#-- @ftlvariable name="date" type="java.lang.Long" -->
<#-- @ftlvariable name="code" type="java.lang.String" -->
<#-- @ftlvariable name="kweet" type="model.Notice" -->
<#import "template.ftl" as layout />

<@layout.mainLayout title="New Post">
<section class="post">
    <header class="post-header">
        <p class="post-meta">
            <a href="/kweet/${kweet.id}">${kweet.date}</a>
            by <a href="/user/${kweet.userId}">${kweet.userId}</a></p>
    </header>
    <div class="post-description">${kweet.text}</div>
</section>
<#if user??>
<p>
    <a href="javascript:void(0)" onclick="document.getElementById('deleteForm').submit()">Delete Post</a>
</p>

<form id="deleteForm" method="post" action="/kweet/${kweet.id}/routes.delete" enctype="application/x-www-form-urlencoded">
    <input type="hidden" name="date" value="${date?c}">
    <input type="hidden" name="code" value="${code}">
</form>
</#if>

</@layout.mainLayout>