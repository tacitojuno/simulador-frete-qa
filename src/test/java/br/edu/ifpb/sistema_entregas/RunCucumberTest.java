package br.edu.ifpb.sistema_entregas;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features") //Aponta para simulacao_frete.feature
public class RunCucumberTest {
}