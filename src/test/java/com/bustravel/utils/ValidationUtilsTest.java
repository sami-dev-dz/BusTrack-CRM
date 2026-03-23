package com.bustravel.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ValidationUtilsTest {

    @Test
    public void testIsValidEmail() {
        assertTrue(ValidationUtils.isValidEmail("test@example.com"));
        assertTrue(ValidationUtils.isValidEmail("user.name+tag@sub.domain.org"));
        
        assertFalse(ValidationUtils.isValidEmail("plainaddress"));
        assertFalse(ValidationUtils.isValidEmail("@missingusername.com"));
        assertFalse(ValidationUtils.isValidEmail("missingdomain@.com"));
        assertFalse(ValidationUtils.isValidEmail(null));
        assertFalse(ValidationUtils.isValidEmail(""));
    }

    @Test
    public void testIsValidPhone() {
        assertTrue(ValidationUtils.isValidPhone("12345678"));
        assertTrue(ValidationUtils.isValidPhone("+33123456789"));
        assertTrue(ValidationUtils.isValidPhone("06 12 34 56 78"));
        
        assertFalse(ValidationUtils.isValidPhone("123")); // too short
        assertFalse(ValidationUtils.isValidPhone("abcdefgh")); // letters
        assertFalse(ValidationUtils.isValidPhone(null));
        assertFalse(ValidationUtils.isValidPhone(""));
    }

    @Test
    public void testIsNotEmpty() {
        assertTrue(ValidationUtils.isNotEmpty("hello"));
        assertTrue(ValidationUtils.isNotEmpty("   world   "));
        
        assertFalse(ValidationUtils.isNotEmpty(null));
        assertFalse(ValidationUtils.isNotEmpty(""));
        assertFalse(ValidationUtils.isNotEmpty("   "));
    }
}
