function requireEnvironmentVariable(key) {
    const value = process.env[key];
    if (!value) {
        throw new Error(`Required environment variable ${key} is missing`);
    }
    return value;
}

export default {
    buletinaBaseUrl: requireEnvironmentVariable('BULETINA_BASE_URL'),
    smtpBaseUrl: requireEnvironmentVariable('SMTP_BASE_URL'),
}
