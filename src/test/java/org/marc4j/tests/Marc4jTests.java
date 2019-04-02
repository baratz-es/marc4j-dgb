package org.marc4j.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    TagTest.class,
    SubfieldTest.class,
    ControlFieldTest.class
})
public class Marc4jTests
{
}
