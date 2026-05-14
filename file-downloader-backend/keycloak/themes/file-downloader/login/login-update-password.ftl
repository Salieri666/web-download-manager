<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
  <#if section = "title">
    ${msg("updatePasswordTitle")}
  <#elseif section = "header">
    ${msg("updatePasswordTitle")}
  <#elseif section = "form">
    <form id="kc-passwd-update-form" action="${url.loginAction}" method="post">
      <div class="field-group">
        <label for="password-new" class="field-label">${msg("passwordNew")}</label>
        <div class="password-wrapper">
          <input
            id="password-new"
            class="field-input"
            name="password-new"
            type="password"
            autofocus
            autocomplete="new-password"
            placeholder="${msg("passwordNew")}"
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

      <div class="actions">
        <button type="submit" class="btn btn-primary">${msg("doSubmit")}</button>
      </div>
    </form>
  </#if>
</@layout.registrationLayout>
