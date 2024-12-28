package com.ontimize.cache;

import java.util.List;

public interface CachedComponent {

    public String getEntity();

    public List getAttributes();

    public void setCacheManager(CacheManager c);

}
