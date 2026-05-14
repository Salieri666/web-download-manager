<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=social.displayInfo displayWide=(realm.password && social.providers??); section>
  <#if section = "title">
    Login
  <#elseif section = "header">
    Login
  <#elseif section = "form">
    <#if realm.password>
      <form id="kc-form-login" action="${url.loginAction}" method="post">
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
            value="${login.username!""}"
            type="text"
            autofocus
            autocomplete="username"
            placeholder="${msg("usernameOrEmail")}"
          />
        </div>

        <div class="field-group">
          <label for="password" class="field-label">${msg("password")}</label>
          <div class="password-wrapper">
            <input
              id="password"
              class="field-input"
              name="password"
              type="password"
              autocomplete="current-password"
              placeholder="${msg("password")}"
            />
          </div>
        </div>

        <#if realm.rememberMe && !usernameEditDisabled??>
          <div class="checkbox-group">
            <label class="checkbox-label">
              <#if login.rememberMe??>
                <input id="rememberMe" name="rememberMe" type="checkbox" checked tabindex="3" />
              <#else>
                <input id="rememberMe" name="rememberMe" type="checkbox" tabindex="3" />
              </#if>
              <span>${msg("rememberMe")}</span>
            </label>
          </div>
        </#if>

        <div class="actions">
          <button type="submit" class="btn btn-primary">${msg("doLogIn")}</button>
        </div>

        <div class="links">
          <#if realm.resetPasswordAllowed>
            <a href="${url.loginResetCredentialsUrl}" class="link">${msg("doForgotPassword")}</a>
          </#if>
          <#if realm.password && realm.registrationAllowed && !usernameEditDisabled??>
            <a href="${url.registrationUrl}" class="link">${msg("doRegister")}</a>
          </#if>
        </div>
      </form>
    </#if>
  <#elseif section = "socialProviders">
    <#if realm.password && social.providers??>
      <div class="social-section">
        <div class="divider">
          <span>${msg("identityProviderLoginLabel")}</span>
        </div>
        <div class="social-buttons">
          <#list social.providers as p>
            <a href="${p.loginUrl}" class="btn btn-social" id="social-${p.alias}">
              <#if p.iconClasses??>
                <i class="${p.iconClasses!}" aria-hidden="true"></i>
              </#if>
              ${p.displayName}
            </a>
          </#list>
        </div>
      </div>
    </#if>
  <#elseif section = "info">
    <#if realm.password && realm.registrationAllowed && !usernameEditDisabled??>
      <div id="kc-info-wrapper">
        <div id="kc-registration">
          <span>${msg("noAccount")} <a href="${url.registrationUrl}">${msg("doRegister")}</a></span>
        </div>
      </div>
    </#if>
  </#if>
</@layout.registrationLayout>
