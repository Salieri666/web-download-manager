<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
  <#if section = "title">
    ${msg("registerTitle")}
  <#elseif section = "header">
    ${msg("registerTitle")}
  <#elseif section = "form">
    <form id="kc-register-form" action="${url.registrationAction}" method="post">
      <div class="field-group">
        <label for="firstName" class="field-label">${msg("firstName")}</label>
        <input
          id="firstName"
          class="field-input"
          name="firstName"
          type="text"
          value="${(register.formData.firstName!'')}"
          autofocus
          autocomplete="given-name"
          placeholder="${msg("firstName")}"
        />
      </div>

      <div class="field-group">
        <label for="lastName" class="field-label">${msg("lastName")}</label>
        <input
          id="lastName"
          class="field-input"
          name="lastName"
          type="text"
          value="${(register.formData.lastName!'')}"
          autocomplete="family-name"
          placeholder="${msg("lastName")}"
        />
      </div>

      <div class="field-group">
        <label for="email" class="field-label">${msg("email")}</label>
        <input
          id="email"
          class="field-input"
          name="email"
          type="email"
          value="${(register.formData.email!'')}"
          autocomplete="email"
          placeholder="${msg("email")}"
        />
      </div>

      <#if !realm.registrationEmailAsUsername>
        <div class="field-group">
          <label for="username" class="field-label">${msg("username")}</label>
          <input
            id="username"
            class="field-input"
            name="username"
            type="text"
            value="${(register.formData.username!'')}"
            autocomplete="username"
            placeholder="${msg("username")}"
          />
        </div>
      </#if>

      <#if passwordRequired??>
        <div class="field-group">
          <label for="password" class="field-label">${msg("password")}</label>
          <div class="password-wrapper">
            <input
              id="password"
              class="field-input"
              name="password"
              type="password"
              autocomplete="new-password"
              placeholder="${msg("password")}"
            />
          </div>
        </div>

        <div class="field-group">
          <label for="password-confirm" class="field-label">${msg("passwordConfirm")}</label>
          <div class="password-wrapper">
            <input
              id="password-confirm"
              class="field-input"
              name="password-confirm"
              type="password"
              autocomplete="new-password"
              placeholder="${msg("passwordConfirm")}"
            />
          </div>
        </div>
      </#if>

      <#if recaptchaRequired??>
        <div class="field-group">
          <div class="g-recaptcha" data-sitekey="${recaptchaSiteKey}"></div>
        </div>
      </#if>

      <div class="actions">
        <button type="submit" class="btn btn-primary">${msg("doRegister")}</button>
      </div>

      <div class="links">
        <a href="${url.loginUrl}" class="link">Back to Login</a>
      </div>
    </form>
  </#if>
</@layout.registrationLayout>
