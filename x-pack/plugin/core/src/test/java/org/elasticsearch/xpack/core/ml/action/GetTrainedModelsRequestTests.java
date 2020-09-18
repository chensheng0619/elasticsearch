/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.xpack.core.ml.action;

import org.elasticsearch.Version;
import org.elasticsearch.common.io.stream.Writeable;
import org.elasticsearch.xpack.core.action.util.PageParams;
import org.elasticsearch.xpack.core.ml.AbstractBWCWireSerializationTestCase;
import org.elasticsearch.xpack.core.ml.action.GetTrainedModelsAction.Request;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GetTrainedModelsRequestTests extends AbstractBWCWireSerializationTestCase<Request> {

    @Override
    protected Request createTestInstance() {
        Request request = new Request(randomAlphaOfLength(20),
            randomBoolean() ? null :
            randomList(10, () -> randomAlphaOfLength(10)),
            randomBoolean() ? null :
                Stream.generate(() -> randomFrom(Request.DEFINITION, Request.TOTAL_FEATURE_IMPORTANCE))
                    .limit(4)
                    .collect(Collectors.toSet()));
        request.setPageParams(new PageParams(randomIntBetween(0, 100), randomIntBetween(0, 100)));
        return request;
    }

    @Override
    protected Writeable.Reader<Request> instanceReader() {
        return Request::new;
    }

    @Override
    protected Request mutateInstanceForVersion(Request instance, Version version) {
        if (version.before(Version.V_7_10_0)) {
            Set<String> includes = new HashSet<>();
            if (instance.isIncludeModelDefinition()) {
                includes.add(Request.DEFINITION);
            }
            Request request = new Request(
                instance.getResourceId(),
                version.before(Version.V_7_7_0) ? null : instance.getTags(),
                includes);
            request.setPageParams(instance.getPageParams());
            request.setAllowNoResources(instance.isAllowNoResources());
            return request;
        }
        return instance;
    }
}
