package com.example.dubbo;

import com.alibaba.csp.sentinel.adapter.dubbo3.fallback.DubboFallback;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.apache.dubbo.rpc.AsyncRpcResult;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;

public class DubboProviderFallBack implements DubboFallback {
    @Override
    public Result handle(Invoker<?> invoker, Invocation invocation, BlockException ex) {
        return AsyncRpcResult.newDefaultAsyncResult(ex.toRuntimeException(), invocation);
    }
}
