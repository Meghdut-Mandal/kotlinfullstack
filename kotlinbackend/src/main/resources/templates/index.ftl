<#-- @ftlvariable name="top" type="java.util.List<model.Notice>" -->
<#-- @ftlvariable name="latest" type="java.util.List<model.Notice>" -->

<#import "template.ftl" as layout />

<@layout.mainLayout title="Welcome">
    <div class="posts">
        <h3 class="content-subhead">Top 10</h3>
        <@layout.kweets_list kweets=top></@layout.kweets_list>

        <h3 class="content-subhead">Recent 10</h3>
        <@layout.kweets_list kweets=latest></@layout.kweets_list>
</div>
</@layout.mainLayout>
