<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true; section>
  <#if section = "title">
    ${msg("emailForgotTitle")}
  <#elseif section = "header">
    ${msg("emailForgotTitle")}
  <#elseif section = "form">
    <form id="kc-reset-password-form" action="${url.loginAction}" method="post">
      <div class="field-group">
        <label for="username" class="field-label">
          <#if !realm.loginWithEmailAllowed>
            ${msg("username")}
          <#elseif realm.registrationEmailAsUsername>
            ${msg("email")}
          <#else>
            ${msg("usernameOrEmail")}
          </#if>
        </label>
        <input
          id="username"
          class="field-input"
          name="username"
          type="text"
          autofocus
          autocomplete="username"
          placeholder="${msg("usernameOrEmail")}"
        />
      </div>

      <div class="actions">
        <button type="submit" class="btn btn-primary">${msg("doSubmit")}</button>
      </div>

      <div class="links">
        <a href="${url.loginUrl}" class="link">Back to Login</a>
      </div>
    </form>
  <#elseif section = "info">
    ${msg("emailInstruction")}
  </#if>
</@layout.registrationLayout>
