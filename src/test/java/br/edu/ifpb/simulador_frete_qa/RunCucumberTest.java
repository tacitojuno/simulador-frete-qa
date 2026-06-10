package br.edu.ifpb.simulador_frete_qa;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features") //Aponta para simulacao_frete.feature
public class RunCucumberTest {
}