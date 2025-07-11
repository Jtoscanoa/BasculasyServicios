package com.puropoo.proyectobys;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for SecondVisit model class
 */
public class SecondVisitTest {

    @Test
    public void secondVisit_constructorWithId_isCorrect() {
        SecondVisit secondVisit = new SecondVisit(1, 123, "Mantenimiento Preventivo", "2024-01-15", "10:30", "12345678");
        
        assertEquals(1, secondVisit.getId());
        assertEquals(123, secondVisit.getServiceRequestId());
        assertEquals("Mantenimiento Preventivo", secondVisit.getServiceType());
        assertEquals("2024-01-15", secondVisit.getVisitDate());
        assertEquals("10:30", secondVisit.getVisitTime());
        assertEquals("12345678", secondVisit.getClientCedula());
    }

    @Test
    public void secondVisit_constructorWithoutId_isCorrect() {
        SecondVisit secondVisit = new SecondVisit(123, "Reparación Técnica", "2024-01-15", "14:00", "87654321");
        
        assertEquals(123, secondVisit.getServiceRequestId());
        assertEquals("Reparación Técnica", secondVisit.getServiceType());
        assertEquals("2024-01-15", secondVisit.getVisitDate());
        assertEquals("14:00", secondVisit.getVisitTime());
        assertEquals("87654321", secondVisit.getClientCedula());
    }

    @Test
    public void secondVisit_setters_workCorrectly() {
        SecondVisit secondVisit = new SecondVisit(123, "Mantenimiento", "2024-01-15", "10:30", "12345678");
        
        secondVisit.setId(999);
        secondVisit.setServiceRequestId(456);
        secondVisit.setServiceType("Reparación");
        secondVisit.setVisitDate("2024-02-20");
        secondVisit.setVisitTime("16:45");
        secondVisit.setClientCedula("11111111");
        
        assertEquals(999, secondVisit.getId());
        assertEquals(456, secondVisit.getServiceRequestId());
        assertEquals("Reparación", secondVisit.getServiceType());
        assertEquals("2024-02-20", secondVisit.getVisitDate());
        assertEquals("16:45", secondVisit.getVisitTime());
        assertEquals("11111111", secondVisit.getClientCedula());
    }

    @Test
    public void secondVisit_validTimeFormats_areAccepted() {
        SecondVisit secondVisit1 = new SecondVisit(1, 123, "Mantenimiento", "2024-01-15", "06:00", "12345678");
        SecondVisit secondVisit2 = new SecondVisit(2, 124, "Mantenimiento", "2024-01-15", "19:00", "12345678");
        
        assertEquals("06:00", secondVisit1.getVisitTime());
        assertEquals("19:00", secondVisit2.getVisitTime());
    }

    @Test
    public void secondVisit_validDateFormats_areAccepted() {
        SecondVisit secondVisit = new SecondVisit(1, 123, "Mantenimiento", "2024-12-31", "10:30", "12345678");
        
        assertEquals("2024-12-31", secondVisit.getVisitDate());
    }
}