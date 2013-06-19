
import time
import sys

MAX_STEPS = 8

def parseInts(s):
  return [int(p) for p in s.split()]

nk = parseInts(sys.stdin.readline())

n = nk[0]
k = nk[1]

iState = parseInts(sys.stdin.readline())
fState = parseInts(sys.stdin.readline())

t = time.time()

iState = [e-1 for e in iState] 
fState = [e-1 for e in fState] 

class Move(object):
  def __init__(self, fr, to):
    self.fr = fr
    self.to = to
    
  def __str__(self):
    return "%s %s" % (self.fr+1, self.to+1)    

def genAtomicMoves(k):
  return [Move(x, y) for x in xrange(0, k) for y in xrange(0, k) if x != y]

class State(object):
  
  def __init__(self, st):
    self.state = st[:]
    
  def __str__(self):
    return str(self.state)
  
  def __eq__(self, other):
    return self.state == other.state  
  
  def set(self, i, val):
    self.state[i] = val
    return self
  
  def hasSmallerDisks(self, radius, peg):
    for i in xrange(0, radius):
      if self.state[i] == peg:
        return True
    return False  
  
  def apply(self, move):
    for i in xrange(0, len(self.state)):
      if self.state[i] == move.fr and not self.hasSmallerDisks(i, move.to):
        return State(self.state).set(i, move.to);
    return None

class Step(object):
  def __init__(self, state, move, prev = None):
    self.state = state
    self.move = move
    if prev != None:
      self.steps = prev.steps + 1
    else:
      self.steps = 0  
    self.prev = prev
    
  def hasAlready(self, newState):
    step = self;
    while step != None:
      if newState == step.state:
        return True
      step = step.prev;
    return False
    
  def next(self, move):
    newState = self.state.apply(move)
    if newState != None:
      if self.hasAlready(newState):
        return None
      return Step(newState, move, self)
    return None
  
  def moves(self):
    moves = []
    step = self
    while step != None and step.move != None:
      moves.append(step.move)
      step = step.prev
    moves.reverse()
    return moves
  

def solve(step, solutions = []):
  for move in atomicMoves:
    nextStep = step.next(move)
    if nextStep != None:
      if nextStep.state == finalState:
        solutions.append(nextStep)
      elif nextStep.steps <= MAX_STEPS:
        solve(nextStep, solutions)
  return solutions    

atomicMoves = genAtomicMoves(k)

initState = State(iState)
finalState = State(fState)

initStep = Step(initState, None)
      
solutions = solve(initStep)            

if len(solutions) > 0:
  sols = sorted(solutions, lambda x, y: x.steps - y.steps)
  minSteps = sols[0].steps
  solutions = []
  for sol in sols:
    if sol.steps > minSteps:
      break
    solutions.append(sol)

#if len(solutions) > 1:
#  minSteps = min(solutions, key=lambda x: x.steps).steps
#  solutions = filter(lambda s: s.steps == minSteps, solutions)

t = time.time() - t
print("Total time = %s" % (t));

for sol in solutions:
  print(sol.steps)
  for move in sol.moves():
    print(move)  

  