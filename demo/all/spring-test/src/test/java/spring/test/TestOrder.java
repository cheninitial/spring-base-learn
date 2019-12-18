package spring.test;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestOrder {

    @Test
    public void test_03_sdfasdsadfasdfasfdsjd() {
        System.out.println("test_03_sdfasdsadfasdfasfdsjd()");
    }

    @Test
    public void test_01() {
        System.out.println("test_01()");
    }

    @Test
    public void test_02_asjdfasjd() {
        System.out.println("test_02_asjdfasjd()");
    }



}
