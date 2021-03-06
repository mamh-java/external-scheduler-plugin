/*
 * The MIT License
 *
 * Copyright (c) 2012 Red Hat, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jenkinsci.plugins.externalscheduler;

import hudson.model.AbstractCIBase;
import hudson.model.Computer;
import hudson.model.Node;
import hudson.model.Queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulate Jenkins state
 *
 * @author ogondza
 */
public class AbstractCiStateProvider implements StateProvider {

    private final AbstractCIBase jenkins;

    /*package*/ AbstractCiStateProvider(final AbstractCIBase base) {

        if (base == null) throw new IllegalArgumentException("Base is null");

        this.jenkins = base;
    }

    /**
     * Get nodes ready to execute builds
     *
     * @return List of online Nodes. Never null
     */
    public List<Node> getNodes() {

        final List<Node> nodeCandidates = new ArrayList<Node>(jenkins.getNodes());
        nodeCandidates.add(jenkins);

        final List<Node> nodes = new ArrayList<Node>();
        for (final Node nodeCadidate: nodeCandidates) {

            if (nodeReady(nodeCadidate)) {

                nodes.add(nodeCadidate);
            }
        }

        return nodes;
    }

    private boolean nodeReady(final Node node) {

        final Computer computer = node.toComputer();
        if (computer == null) return false;

        return !computer.isOffline() && computer.isAcceptingTasks();
    }

    /**
     * Get buildable items to schedule
     *
     * @return List of queued items to be scheduled. Never null
     */
    public List<Queue.BuildableItem> getQueue() {

        return Collections.unmodifiableList(
                jenkins.getQueue().getBuildableItems()
        );
    }
}
