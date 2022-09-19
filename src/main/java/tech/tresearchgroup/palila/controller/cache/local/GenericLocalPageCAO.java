package tech.tresearchgroup.palila.controller.cache.local;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import tech.tresearchgroup.palila.model.BaseSettings;

public class GenericLocalPageCAO implements BasicLocalPageCache {
    private final Cache<String, byte[]> apiPageCache;

    public GenericLocalPageCAO() {
        this.apiPageCache = Caffeine.newBuilder().maximumSize(BaseSettings.pageCacheSize).build();
    }

    @Override
    public void create(String location, long page, long maxResults, byte[] data) {
        apiPageCache.put(location + "-" + page + "-" + maxResults, data);
    }

    @Override
    public byte[] read(String location, long page, long maxResults) {
        return apiPageCache.getIfPresent(location + "-" + page + "-" + maxResults);
    }

    @Override
    public void delete() {
        apiPageCache.invalidateAll();
    }
}
