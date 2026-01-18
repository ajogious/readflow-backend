package com.readflow.readflow_backend.service;

public class EmailTemplates {

  private EmailTemplates() {
  }

  public static String verificationEmail(String verifyLink) {
    return """
            <div style="font-family:Arial,sans-serif;line-height:1.5">
              <h2>Verify your ReadFlow account</h2>
              <p>Click the button below to verify your email address:</p>
              <p><a href="%s" style="display:inline-block;padding:10px 14px;background:#2563eb;color:#fff;text-decoration:none;border-radius:6px">Verify Email</a></p>
              <p>If the button doesn’t work, copy and paste this link:</p>
              <p><a href="%s">%s</a></p>
              <p>This link expires in 24 hours.</p>
            </div>
        """
        .formatted(verifyLink, verifyLink, verifyLink);
  }

  public static String resetPasswordEmail(String resetLink) {
    return """
            <div style="font-family:Arial,sans-serif;line-height:1.5">
              <h2>Reset your ReadFlow password</h2>
              <p>Click the button below to set a new password:</p>
              <p><a href="%s" style="display:inline-block;padding:10px 14px;background:#16a34a;color:#fff;text-decoration:none;border-radius:6px">Reset Password</a></p>
              <p>If the button doesn’t work, copy and paste this link:</p>
              <p><a href="%s">%s</a></p>
              <p>This link expires in 1 hour. If you didn’t request it, ignore this email.</p>
            </div>
        """
        .formatted(resetLink, resetLink, resetLink);
  }
}
