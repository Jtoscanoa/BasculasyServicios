package com.puropoo.proyectobys;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test for the installation service filtering logic
 */
public class DatabaseHelperInstallationTest {

    @Test
    public void testInstallationServiceTypeMatching() {
        // Test cases with different service types
        String[] installationTypes = {
            "Instalación de equipos",
            "Instalación",
            "Install equipment",
            "installation service",
            "INSTALACIÓN DE BASCULAS"
        };
        
        String[] nonInstallationTypes = {
            "Mantenimiento",
            "Repair",
            "Service",
            "Calibración",
            "Revisión"
        };
        
        // Test that installation types are correctly identified
        for (String serviceType : installationTypes) {
            boolean isInstallation = isInstallationService(serviceType);
            assertTrue("Service type '" + serviceType + "' should be identified as installation", isInstallation);
        }
        
        // Test that non-installation types are correctly filtered out
        for (String serviceType : nonInstallationTypes) {
            boolean isInstallation = isInstallationService(serviceType);
            assertFalse("Service type '" + serviceType + "' should NOT be identified as installation", isInstallation);
        }
    }

    /**
     * Helper method that mimics the database query logic
     */
    private boolean isInstallationService(String serviceType) {
        if (serviceType == null) return false;
        String lowerServiceType = serviceType.toLowerCase();
        return lowerServiceType.contains("install") || lowerServiceType.contains("instalac");
    }
    
    @Test
    public void testEmptyAndNullServiceTypes() {
        assertFalse("Null service type should return false", isInstallationService(null));
        assertFalse("Empty service type should return false", isInstallationService(""));
        assertFalse("Whitespace service type should return false", isInstallationService("   "));
    }
}