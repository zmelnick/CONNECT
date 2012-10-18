/**
 * Copyright (c) 2012, United States Government, as represented by the Secretary of Health and Human Services.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above
 *       copyright notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * Neither the name of the United States Government nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package gov.hhs.fha.nhinc.subscribe.aspect;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import gov.hhs.fha.nhinc.aspect.EventAspectAdvice;

/**
 * @author zmelnick
 *
 */
@Aspect
public class SubscribeEventAspect extends EventAspectAdvice {

    /*--- Inbound Message ---*/
    @Pointcut("execution(* gov.hhs.fha.nhinc.hiem.*.subscribe.nhin.HiemSubscribe.subscribe(..))")
    private void inboundMessage() {
    }

    @Override
    @Before("inboundMessage()")
    public void beginInboundMessageEvent() {
        super.beginInboundMessageEvent();
    }

    @Override
    @After("inboundMessage()")
    public void endInboundMessageEvent() {
        super.endInboundMessageEvent();
    }

    /*--- Inbound Processing ---*/
    @Pointcut("execution(* gov.hhs.fha.nhinc.hiem.*.subscribe.nhin.HiemSubscribeImpl.subscribe(..))")
    private void inboundProcessing() {
    }

    @Override
    @Before("inboundProcessing()")
    public void beginInboundProcessingEvent() {
        super.beginInboundProcessingEvent();
    }

    @Override
    @After("inboundProcessing()")
    public void endInboundProcessingEvent() {
        super.endInboundProcessingEvent();
    }

    /*--- Adapter Delegation ---*/
    @Pointcut("execution(* gov.hhs.fha.nhinc.subscribe.adapter.proxy.HiemSubscribeAdapter*.subscribe(..))")
    private void adapterDelegation() {
    }

    @Override
    @Before("adapterDelegation()")
    public void beginAdapterDelegationEvent() {
        super.beginAdapterDelegationEvent();
    }

    @Override
    @After("adapterDelegation()")
    public void endAdapterDelegationEvent() {
        super.endAdapterDelegationEvent();
    }

    /*--- Outbound Message ---*/
    @Pointcut("execution(* gov.hhs.fha.nhinc.hiem.*.subscribe.entity.EntitySubscribeService.subscribe(..))")
    private void outboundMessageEntityUnsecured(){
    }

    @Pointcut("execution(* gov.hhs.fha.nhinc.hiem.*.subscribe.entity.EntitySubscribeSecuredService.subscribe(..))")
    private void outboundMessageEntitySecured(){
    }

    @Pointcut("outboundMessageEntityUnsecured() || outboundMessageEntitySecured()")
    private void entityOutboundMessage() {
    }

    @Pointcut("execution(* gov.hhs.fha.nhinc.hiem.*.subscribe.passthru.ProxyHiemSubscribe.subscribe(..))")
    private void outboundMessagePassthruUnsecured(){
    }

    @Pointcut("execution(* gov.hhs.fha.nhinc.hiem.*.subscribe.passthru.ProxyHiemSubscribeSecured.subscribe(..))")
    private void outboundMessagePassthruSecured(){
    }

    @Pointcut("outboundMessageEntityUnsecured() || outboundMessageEntitySecured()")
    private void passthruOutboundMessage() {
    }

    @Override
    @Before("entityOutboundMessage() || passthruOutboundMessage()")
    public void beginOutboundMessageEvent() {
        super.beginOutboundMessageEvent();
    }

    @Override
    @After("entityOutboundMessage() || passthruOutboundMessage()")
    public void endOutboundMessageEvent() {
        super.endOutboundMessageEvent();
    }

    /*--- Outbound Processing ---*/

    @Pointcut("execution(* gov.hhs.fha.nhinc.subscribe.entity.EntitySubscribeOrchImpl.processSubscribe(..))")
    private void entityOutboundProcessing() {
    }

    @Pointcut("execution(* gov.hhs.fha.nhinc.hiem.*.subscribe.passthru.ProxyHiemSubscribeImpl.subscribe(..))")
    private void passthruOutboundProcessing() {
    }

    @Override
    @Before("entityOutboundProcessing() || passthruOutboundProcessing()")
    public void beginOutboundProcessingEvent() {
        super.beginOutboundProcessingEvent();
    }

    @Override
    @After("entityOutboundProcessing()|| passthruOutboundProcessing()")
    public void endOutboundProcessingEvent() {
        super.endOutboundProcessingEvent();
    }

    /*------ Nwhin Invocation ----*/
    @Pointcut("execution(* gov.hhs.fha.nhinc.subscribe.nhin.proxy.NhinHiemSubscribe*.subscribe(..))")
    private void nhinNwhinInvocation() {
    }

    @Override
    @Before("nhinNwhinInvocation()")
    public void beginNwhinInvocationEvent(){
        super.beginNwhinInvocationEvent();
    }

    @Override
    @After("nhinNwhinInvocation()")
    public void endNwhinInvocationEvent(){
        super.endNwhinInvocationEvent();
    }

    /*------ Failure ----*/
    @Override
    @AfterThrowing("inboundMessage() || entityOutboundMessage() || passthruOutboundMessage()")
    public void failEvent() {
        super.failEvent();
    }
}