package com.fredtargaryen.fragileglass.entity.capability;

import java.util.concurrent.Callable;

public class ClientCanBreakFactory implements Callable<IClientCanBreakCapability>
{
    @Override
    public IClientCanBreakCapability call() throws Exception {
        return new ClientCanBreakImpl();
    }

    private class ClientCanBreakImpl implements IClientCanBreakCapability
    {
    }
}
