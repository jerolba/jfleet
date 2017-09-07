/**
 * Copyright 2017 Jerónimo López Bezanilla
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jfleet.util.jpa;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;

import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;

public class EntityManagerFactoryFactory {

    private Class<?>[] entities;
    private DataSource dataSource;

    public EntityManagerFactoryFactory(DataSource dataSource, Class<?> ...entities) {
        this.entities = entities;
        this.dataSource = dataSource;
    }

    public EntityManagerFactory newEntityManagerFactory() {
        String name = getClass().getSimpleName();
        List<String> entiesClassNames = Arrays.asList(entities).stream().map(Class::getName)
                .collect(Collectors.toList());
        PersistenceUnitInfo persistenceUnitInfo = new PersistenceUnitInfoImpl(name, entiesClassNames, properties());
        PersistenceUnitInfoDescriptor puiDesc = new PersistenceUnitInfoDescriptor(persistenceUnitInfo);
        EntityManagerFactoryBuilderImpl entityManagerFactoryBuilder = new EntityManagerFactoryBuilderImpl(
                puiDesc, new HashMap<>());
        return entityManagerFactoryBuilder.build();
    }

    protected DataSource getDataSource() {
        return dataSource;
    }

    protected Properties properties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        properties.put("hibernate.connection.datasource", getDataSource());
        properties.put("hibernate.generate_statistics", Boolean.TRUE.toString());
        return properties;
    }

}
