package com.mwb.maf.core.rpc;

import com.weibo.api.motan.config.springsupport.*;

public abstract class AbstractMotanConfiguration extends BaseMotanConfiguration {

    protected abstract AnnotationBean annotationBean();

    protected abstract ProtocolConfigBean protocolConfigBean();

    protected abstract RegistryConfigBean registryConfigBean();

    protected abstract BasicServiceConfigBean basicServiceConfigBean(
            RegistryConfigBean defaultRegistry,
            ProtocolConfigBean defaultProtocol);

    protected abstract BasicRefererConfigBean basicRefererConfigBean(
            RegistryConfigBean defaultRegistry,
            ProtocolConfigBean defaultProtocol);

}
