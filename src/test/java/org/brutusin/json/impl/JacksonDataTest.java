/*
 * Copyright 2015 Ignacio del Valle Alles idelvall@brutusin.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.brutusin.json.impl;

import org.brutusin.json.ParseException;
import org.brutusin.json.spi.DataCodecTest;
import org.brutusin.json.spi.JsonCodec;
import org.junit.Test;

/**
 *
 * @author Ignacio del Valle Alles idelvall@brutusin.org
 */
public class JacksonDataTest extends DataCodecTest {

    @Test
    public void testComposite() throws ParseException{
        A a = new A();
        B b = new B();
        C c = new C();
        
        c.name="Nacho";
        b.c=c;
        a.b=b;
        c.name="aaaaaa\nsadasdasdas\n\tdfsdfsd";
        System.out.println(JsonCodec.getInstance().transform(a));
        
    }

    class A {
        B b;

        public B getB() {
            return b;
        }
        public void setB(B b) {
            this.b = b;
        }
    }

    class B {
        C c;
        public C getC() {
            return c;
        }
        public void setC(C c) {
            this.c = c;
        }
    }

    class C {
        String name;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }
}
